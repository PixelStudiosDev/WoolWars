package me.cubecrafter.woolwars.listeners;

import me.cubecrafter.woolwars.api.arena.GameState;
import me.cubecrafter.woolwars.arena.GameArena;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.team.GameTeam;
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
        GameArena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena != null) {
            if (arena.isAlive(player)) {
                setRecipients(e, arena.getAlivePlayers());
                if (arena.getGameState().equals(GameState.STARTING) || arena.getGameState().equals(GameState.WAITING)) {
                    e.setFormat(TextUtil.format(Configuration.WAITING_LOBBY_CHAT_FORMAT.getAsString()
                            .replace("{player}", player.getDisplayName())
                            .replace("{message}", e.getMessage()), player));
                } else {
                    GameTeam team = arena.getTeamByPlayer(player);
                    e.setFormat(TextUtil.format(Configuration.GAME_CHAT_FORMAT.getAsString()
                            .replace("{player}", player.getDisplayName())
                            .replace("{team_color}", team.getTeamColor().getChatColor().toString())
                            .replace("{team}", team.getName())
                            .replace("{message}", e.getMessage()), player));
                }
            } else {
                setRecipients(e, arena.getDeadPlayers());
                e.setFormat(TextUtil.format(Configuration.SPECTATOR_CHAT_FORMAT.getAsString()
                        .replace("{player}", player.getDisplayName())
                        .replace("{message}", e.getMessage()), player));
            }
        } else {
            setRecipients(e, Bukkit.getOnlinePlayers().stream().filter(p -> !ArenaUtil.isPlaying(player)).collect(Collectors.toList()));
            e.setFormat(TextUtil.format(Configuration.LOBBY_CHAT_FORMAT.getAsString()
                    .replace("{player}", player.getDisplayName())
                    .replace("{message}", e.getMessage()), player));
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        List<String> commands = Configuration.BLOCKED_COMMANDS.getAsStringList();
        if (Configuration.BLOCKED_COMMANDS_WHITELIST.getAsBoolean()) {
            if (commands.stream().noneMatch(command -> e.getMessage().toLowerCase().startsWith("/" + command.toLowerCase()))) {
                e.setCancelled(true);
                player.sendMessage(TextUtil.color("You can't use this command while you are in game!"));
            }
        } else {
            if (commands.stream().anyMatch(command -> e.getMessage().toLowerCase().startsWith("/" + command.toLowerCase()))) {
                e.setCancelled(true);
                player.sendMessage(TextUtil.color("You can't use this command while you are in game!"));
            }
        }
    }

    private void setRecipients(AsyncPlayerChatEvent e, List<Player> recipients) {
        e.getRecipients().clear();
        e.getRecipients().addAll(recipients);
    }

}
