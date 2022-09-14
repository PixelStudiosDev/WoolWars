/*
 * Wool Wars
 * Copyright (C) 2022 CubeCrafter Development
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.cubecrafter.woolwars.database;

import me.cubecrafter.woolwars.WoolWars;
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

    private final Database database;

    public PlayerDataManager(WoolWars plugin) {
        database = plugin.getStorage();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private final Map<UUID, PlayerData> playerData = new HashMap<>();

    public PlayerData getPlayerData(Player player) {
        return playerData.get(player.getUniqueId());
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        if (e.getResult() != PlayerLoginEvent.Result.ALLOWED) return;
        Player player = e.getPlayer();
        PlayerData data = database.getPlayerData(player.getUniqueId());
        playerData.put(player.getUniqueId(), data);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        database.savePlayerDataAsync(getPlayerData(player));
        playerData.remove(player.getUniqueId());
    }

    public void save() {
        for (PlayerData data : playerData.values()) {
            database.savePlayerData(data);
        }
    }

    public void load() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerData data = database.getPlayerData(player.getUniqueId());
            playerData.put(player.getUniqueId(), data);
        }
    }

}
