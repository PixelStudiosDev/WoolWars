package me.cubecrafter.woolwars.database;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.api.database.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager implements Listener {

    private final Database database = WoolWars.getInstance().getSQLDatabase();

    public PlayerDataManager() {
        WoolWars.getInstance().getServer().getPluginManager().registerEvents(this, WoolWars.getInstance());
    }

    private final Map<UUID, PlayerData> playerData = new HashMap<>();

    public PlayerData getPlayerData(Player player) {
        return playerData.get(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerJoin(PlayerLoginEvent e) {
        Player player = e.getPlayer();
        PlayerData data = database.getPlayerData(player.getUniqueId());
        playerData.put(player.getUniqueId(), data);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        database.savePlayerDataAsync(getPlayerData(player));
        playerData.remove(player.getUniqueId());
    }

    public void forceSave() {
        for (PlayerData data : playerData.values()) {
            database.savePlayerData(data);
        }
    }

    public void forceLoad() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerData data = database.getPlayerData(player.getUniqueId());
            playerData.put(player.getUniqueId(), data);
        }
    }

}
