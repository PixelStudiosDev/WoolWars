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

package me.cubecrafter.woolwars.api;

import lombok.experimental.UtilityClass;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.storage.PlayerData;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.kits.Kit;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

@UtilityClass
public class WoolWarsAPI {

    public Location getLobbyLocation() {
        return Config.LOBBY_LOCATION.getAsLocation();
    }

    public List<Arena> getArenas() {
        return ArenaUtil.getArenas();
    }

    public Arena getArenaByPlayer(Player player) {
        return ArenaUtil.getArenaByPlayer(player);
    }

    public Arena getArenaById(String id) {
        return ArenaUtil.getArenaById(id);
    }

    public List<Arena> getArenasByGroup(String group) {
        return ArenaUtil.getArenasByGroup(group);
    }

    public List<String> getGroups() {
        return ArenaUtil.getGroups();
    }

    public Kit getKitById(String id) {
        return ArenaUtil.getKit(id);
    }

    public List<Kit> getKits() {
        return ArenaUtil.getKits();
    }

    public PlayerData getPlayerData(Player player) {
        return ArenaUtil.getPlayerData(player);
    }

    public boolean isPlaying(Player player) {
        return ArenaUtil.isPlaying(player);
    }

    public void joinRandomArena(Player player) {
        ArenaUtil.joinRandomArena(player);
    }

    public void joinRandomArena(Player player, String group) {
        ArenaUtil.joinRandomArena(player, group);
    }

}
