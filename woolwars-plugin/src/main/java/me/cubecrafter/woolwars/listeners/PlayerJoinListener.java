package me.cubecrafter.woolwars.listeners;

import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@RequiredArgsConstructor
public class PlayerJoinListener implements Listener {

    private final WoolWars plugin;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage("");
        Player player = e.getPlayer();
        ArenaUtil.teleportToLobby(player);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (ArenaUtil.isPlaying(online)) {
                    System.out.println("Player " + online.getName() + " is playing. Hiding from " + player.getName());
                    plugin.getNms().hidePlayer(player, online);
                    plugin.getNms().hidePlayer(online, player);
                }
            }
        }, 15L);
    }

}
