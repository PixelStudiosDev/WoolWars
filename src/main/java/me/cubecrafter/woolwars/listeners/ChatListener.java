/*
 * Wool Wars
 * Copyright (C) 2023 CubeCrafter Development
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.cubecrafter.woolwars.listeners;

import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.ArenaUtil;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.arena.team.Team;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.storage.player.PlayerManager;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.xutils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;
import java.util.stream.Collectors;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (!Config.CHAT_FORMAT_ENABLED.asBoolean()) return;

        WoolPlayer player = PlayerManager.get(event.getPlayer());
        Arena arena = ArenaUtil.getArenaByPlayer(player);

        if (arena != null) {
            Team team = arena.getTeam(player);
            if (player.isAlive()) {
                setRecipients(event, arena.getAlivePlayers());
                if (arena.getState().equals(GameState.STARTING) || arena.getState().equals(GameState.WAITING)) {
                    event.setFormat(formatMessage(event, player, Config.LOBBY_CHAT_FORMAT.asString()));
                } else {
                    event.setFormat(formatMessage(event, player, Config.GAME_CHAT_FORMAT.asString()
                            .replace("{team_color}", team.getTeamColor().getChatColor().toString())
                            .replace("{team}", team.getName())
                            .replace("{team_letter}", team.getLetter())));
                }
            } else {
                setRecipients(event, arena.getDeadPlayers());
                event.setFormat(formatMessage(event, player, Config.SPECTATOR_CHAT_FORMAT.asString()
                        .replace("{team_color}", team.getTeamColor().getChatColor().toString())
                        .replace("{team}", team.getName())
                        .replace("{team_letter}", team.getLetter())));
            }
        } else {
            setRecipients(event, getLobbyPlayers());
            event.setFormat(formatMessage(event, player, Config.LOBBY_CHAT_FORMAT.asString()));
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("woolwars.bypass")) return;
        if (!ArenaUtil.isPlaying(PlayerManager.get(player))) return;

        List<String> commands = Config.BLOCKED_COMMANDS.asStringList();

        if (Config.BLOCKED_COMMANDS_WHITELIST.asBoolean()) {
            if (commands.stream().noneMatch(command -> event.getMessage().toLowerCase().startsWith('/' + command.toLowerCase()))) {
                event.setCancelled(true);
                TextUtil.sendMessage(player, Messages.COMMAND_BLOCKED.asString());
            }
        } else {
            if (commands.stream().anyMatch(command -> event.getMessage().toLowerCase().startsWith('/' + command.toLowerCase()))) {
                event.setCancelled(true);
                TextUtil.sendMessage(player, Messages.COMMAND_BLOCKED.asString());
            }
        }
    }

    private String formatMessage(AsyncPlayerChatEvent event, WoolPlayer player, String message) {
        message = message.replace("{player}", player.getName()).replace("{message}", event.getMessage());
        return TextUtil.color(ArenaUtil.parsePlaceholders(player, message));
    }

    private void setRecipients(AsyncPlayerChatEvent event, List<WoolPlayer> recipients) {
        event.getRecipients().clear();
        for (WoolPlayer recipient : recipients) {
            event.getRecipients().add(recipient.getPlayer());
        }
    }

    private List<WoolPlayer> getLobbyPlayers() {
        return Bukkit.getOnlinePlayers().stream().map(PlayerManager::get).filter(woolPlayer -> !ArenaUtil.isPlaying(woolPlayer)).collect(Collectors.toList());
    }

}
