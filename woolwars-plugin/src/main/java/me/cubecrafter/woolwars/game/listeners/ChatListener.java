package me.cubecrafter.woolwars.game.listeners;

import me.cubecrafter.woolwars.config.ConfigPath;
import me.cubecrafter.woolwars.game.arena.Arena;
import me.cubecrafter.woolwars.game.team.Team;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;
import java.util.stream.Collectors;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena != null) {
            if (arena.isAlive(player)) {
                setRecipients(e, arena.getAlivePlayers());
            } else {
                setRecipients(e, arena.getDeadPlayers());
            }
            switch (arena.getArenaState()) {
                case WAITING:
                case STARTING:
                    e.setFormat(TextUtil.color("&7{player_name}: {message}"
                            .replace("{player_name}", player.getDisplayName())
                            .replace("{message}", TextUtil.color(e.getMessage()))));
                    break;
                case PLAYING:
                    Team team = arena.getTeamByPlayer(player);
                    e.setFormat(TextUtil.color("{player_teamcolor}{player_team} &7{player_name}: {message}"
                            .replace("{player_name}", player.getDisplayName())
                            .replace("{player_teamcolor}", team.getTeamColor().getChatColor().toString())
                            .replace("{player_team}", team.getName())
                            .replace("{message}", TextUtil.color(e.getMessage()))));
                    break;
            }
        } else {
            setRecipients(e, Bukkit.getOnlinePlayers().stream().filter(p -> ArenaUtil.getArenaByPlayer(p) == null).collect(Collectors.toList()));
            e.setFormat(TextUtil.color("&7{player_name}: {message}"
                    .replace("{player_name}", player.getDisplayName())
                    .replace("{message}", TextUtil.color(e.getMessage()))));
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        if (player.hasPermission("woolwars.admin")) return;
        if (ConfigPath.BLOCKED_COMMANDS.getAsStringList().stream().anyMatch(command -> e.getMessage().toLowerCase().startsWith("/" + command.toLowerCase()))) {
            e.setCancelled(true);
            player.sendMessage(TextUtil.color("You can't use this command while you are in game!"));
        }
    }

    private void setRecipients(AsyncPlayerChatEvent e, List<Player> recipients) {
        e.getRecipients().clear();
        e.getRecipients().addAll(recipients);
    }

}
