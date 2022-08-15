package me.cubecrafter.woolwars.listeners;

import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.VersionUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class JoinQuitListener implements Listener {

    private final WoolWars plugin;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage("");
        Player player = e.getPlayer();
        ArenaUtil.teleportToLobby(player);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (ArenaUtil.isPlaying(online)) {
                    VersionUtil.hidePlayer(player, online);
                    VersionUtil.hidePlayer(online, player);
                }
            }
        }, 15L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        e.setQuitMessage("");
        Player player = e.getPlayer();
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        arena.removePlayer(player, false);
    }

}
