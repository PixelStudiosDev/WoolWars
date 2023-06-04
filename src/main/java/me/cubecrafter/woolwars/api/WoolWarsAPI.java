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

package me.cubecrafter.woolwars.api;

import lombok.experimental.UtilityClass;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.storage.player.PlayerManager;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.kit.Kit;
import me.cubecrafter.woolwars.arena.ArenaUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class WoolWarsAPI {

    public static Location getLobbyLocation() {
        return Config.LOBBY_LOCATION.asLocation();
    }

    public static List<Arena> getArenas() {
        return ArenaUtil.getArenas();
    }

    public static Arena getArenaByPlayer(WoolPlayer player) {
        return ArenaUtil.getArenaByPlayer(player);
    }

    public static Arena getArenaById(String id) {
        return ArenaUtil.getArenaById(id);
    }

    public static List<Arena> getArenasByGroup(String group) {
        return ArenaUtil.getArenasByGroup(group);
    }

    public static List<String> getGroups() {
        return ArenaUtil.getGroups();
    }

    public static Kit getKitById(String id) {
        return WoolWars.get().getKitManager().getKit(id);
    }

    public static List<Kit> getKits() {
        return new ArrayList<>(WoolWars.get().getKitManager().getKits());
    }

    public static WoolPlayer getPlayer(Player player) {
        return PlayerManager.get(player);
    }

    public static boolean isPlaying(WoolPlayer player) {
        return ArenaUtil.isPlaying(player);
    }

    public static void joinRandomArena(WoolPlayer player) {
        ArenaUtil.joinRandomArena(player);
    }

    public static void joinRandomArena(WoolPlayer player, String group) {
        ArenaUtil.joinRandomArena(player, group);
    }

}
