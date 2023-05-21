/*
 * Wool Wars
 * Copyright (C) 2023 CubeCrafter Development
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

package me.cubecrafter.woolwars.storage.player;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.storage.Database;
import me.cubecrafter.xutils.Events;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerManager {

    private final Map<UUID, WoolPlayer> players = new HashMap<>();
    private final Database database;

    public PlayerManager(WoolWars plugin) {
        this.database = plugin.getStorage();

        Events.subscribe(PlayerLoginEvent.class, event -> {
            if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) return;
            loadPlayer(event.getPlayer());
        });

        Events.subscribe(PlayerQuitEvent.class, event -> {
            Player player = event.getPlayer();
            database.saveData(players.remove(player.getUniqueId()));
        });
    }

    public WoolPlayer getPlayer(Player player) {
        return players.get(player.getUniqueId());
    }

    public Collection<WoolPlayer> getPlayers() {
        return players.values();
    }

    private void loadPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        WoolPlayer woolPlayer = new WoolPlayer(player);
        players.put(uuid, woolPlayer);
        database.fetchData(uuid).thenAccept(data -> {
            if (data == null) return;
            woolPlayer.setData(data);
        });
    }

    public void load() {
        Bukkit.getOnlinePlayers().forEach(this::loadPlayer);
    }

    public void save() {
        database.saveAllData(players.values());
    }

    public static WoolPlayer get(Player player) {
        return WoolWars.get().getPlayerManager().getPlayer(player);
    }

    public static Collection<WoolPlayer> getOnline() {
        return WoolWars.get().getPlayerManager().getPlayers();
    }

}
