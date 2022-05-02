package me.cubecrafter.woolwars.listeners;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.teleport(TextUtil.deserializeLocation(WoolWars.getInstance().getFileManager().getConfig().getString("lobby-location")));
        ArenaUtil.hidePlayersInGame(player);
    }

}
