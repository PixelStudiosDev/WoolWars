package me.cubecrafter.woolwars.listeners;

import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.utils.GameUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (GameUtil.isPlaying(player)) {
            Arena arena = GameUtil.getArenaByPlayer(player);
            arena.removePlayer(player);
        }
    }

}
