package me.cubecrafter.woolwars.game.listeners;

import me.cubecrafter.woolwars.game.arena.GameState;
import me.cubecrafter.woolwars.game.arena.Arena;
import me.cubecrafter.woolwars.game.team.Team;
import me.cubecrafter.woolwars.utils.GameUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        Arena arena = GameUtil.getArenaByPlayer(player);
        if (arena != null) {
            if (arena.getGameState().equals(GameState.WAITING) || arena.getGameState().equals(GameState.STARTING)) {
                e.setFormat(TextUtil.color("&7{player_name}: {message}"
                        .replace("{player_name}", player.getDisplayName())
                        .replace("{message}", TextUtil.color(e.getMessage()))));
            } else {
                Team team = arena.getTeamByPlayer(player);
                e.setFormat(TextUtil.color("{player_teamcolor}{player_team} &7{player_name}: {message}"
                        .replace("{player_name}", player.getDisplayName())
                        .replace("{player_teamcolor}", team.getTeamColor().getChatColor().toString())
                        .replace("{player_team}", team.getName())
                        .replace("{message}", TextUtil.color(e.getMessage()))));
            }
        } else {
            e.setFormat(TextUtil.color("&7{player_name}: {message}"
                    .replace("{player_name}", player.getDisplayName())
                    .replace("{message}", TextUtil.color(e.getMessage()))));
        }
    }

}
