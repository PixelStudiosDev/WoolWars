package me.cubecrafter.woolwars.database;

import me.cubecrafter.woolwars.WoolWars;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataHandler implements Listener {

    public PlayerDataHandler() {
        WoolWars.getInstance().getServer().getPluginManager().registerEvents(this, WoolWars.getInstance());
    }

    private final Map<UUID, PlayerData> playerData = new HashMap<>();

    public PlayerData getPlayerData(UUID uuid) {
        return playerData.get(uuid);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        PlayerData data = new PlayerData(player.getUniqueId());
        playerData.put(player.getUniqueId(), data);
    }

}
