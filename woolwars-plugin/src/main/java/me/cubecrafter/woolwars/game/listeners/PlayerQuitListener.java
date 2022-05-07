package me.cubecrafter.woolwars.game.listeners;

import me.cubecrafter.woolwars.game.arena.Arena;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (ArenaUtil.isPlaying(player)) {
            Arena arena = ArenaUtil.getArenaByPlayer(player);
            arena.removePlayer(player, true);
        }
    }

}
