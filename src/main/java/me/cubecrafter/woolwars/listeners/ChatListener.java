/*
 * Wool Wars
 * Copyright (C) 2022 CubeCrafter Development
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
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.team.Team;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
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
    public void onChat(AsyncPlayerChatEvent e) {
        if (!Configuration.CHAT_FORMAT_ENABLED.getAsBoolean()) return;
        Player player = e.getPlayer();
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena != null) {
            if (arena.isAlive(player)) {
                setRecipients(e, arena.getAlivePlayers());
                if (arena.getGameState().equals(GameState.STARTING) || arena.getGameState().equals(GameState.WAITING)) {
                    e.setFormat(TextUtil.format(player, Configuration.WAITING_LOBBY_CHAT_FORMAT.getAsString()
                            .replace("{player}", player.getName())
                            .replace("{message}", e.getMessage())));
                } else {
                    Team team = arena.getTeamByPlayer(player);
                    e.setFormat(TextUtil.format(player, Configuration.GAME_CHAT_FORMAT.getAsString()
                            .replace("{player}", player.getName())
                            .replace("{team_color}", team.getTeamColor().getChatColor().toString())
                            .replace("{team}", team.getName())
                            .replace("{team_letter}", team.getTeamLetter())
                            .replace("{message}", e.getMessage())));
                }
            } else {
                setRecipients(e, arena.getDeadPlayers());
                e.setFormat(TextUtil.format(player, Configuration.SPECTATOR_CHAT_FORMAT.getAsString()
                        .replace("{player}", player.getName())
                        .replace("{message}", e.getMessage())));
            }
        } else {
            setRecipients(e, Bukkit.getOnlinePlayers().stream().filter(p -> !ArenaUtil.isPlaying(p)).collect(Collectors.toList()));
            e.setFormat(TextUtil.format(player, Configuration.LOBBY_CHAT_FORMAT.getAsString()
                    .replace("{player}", player.getName())
                    .replace("{message}", e.getMessage())));
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        if (player.hasPermission("woolwars.bypass")) return;
        if (!ArenaUtil.isPlaying(player)) return;
        List<String> commands = Configuration.BLOCKED_COMMANDS.getAsStringList();
        if (Configuration.BLOCKED_COMMANDS_WHITELIST.getAsBoolean()) {
            if (commands.stream().noneMatch(command -> e.getMessage().toLowerCase().startsWith("/" + command.toLowerCase()))) {
                e.setCancelled(true);
                player.sendMessage(TextUtil.color(Messages.COMMAND_BLOCKED.getAsString()));
            }
        } else {
            if (commands.stream().anyMatch(command -> e.getMessage().toLowerCase().startsWith("/" + command.toLowerCase()))) {
                e.setCancelled(true);
                player.sendMessage(TextUtil.color(Messages.COMMAND_BLOCKED.getAsString()));
            }
        }
    }

    private void setRecipients(AsyncPlayerChatEvent e, List<Player> recipients) {
        e.getRecipients().clear();
        e.getRecipients().addAll(recipients);
    }

}
