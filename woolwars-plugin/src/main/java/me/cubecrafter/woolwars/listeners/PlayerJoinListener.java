package me.cubecrafter.woolwars.listeners;

import me.cubecrafter.woolwars.utils.ArenaUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage("");
        Player player = e.getPlayer();
        ArenaUtil.teleportToLobby(player);
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (ArenaUtil.isPlaying(online)) {
                player.hidePlayer(online);
                online.hidePlayer(player);
            }
        }
    }

}
