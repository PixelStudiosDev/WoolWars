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

package me.cubecrafter.woolwars.arena.setup;

import lombok.Data;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.menu.setup.SetupMenu;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.xutils.FileUtil;
import me.cubecrafter.xutils.TextUtil;
import me.cubecrafter.xutils.menu.Menu;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class SetupSession {

    private static final Map<UUID, SetupSession> sessions = new HashMap<>();

    public static boolean hasSession(WoolPlayer player) {
        return sessions.containsKey(player.getPlayer().getUniqueId());
    }

    public static void remove(WoolPlayer player) {
        sessions.remove(player.getPlayer().getUniqueId());
    }

    public static SetupSession get(WoolPlayer player) {
        return sessions.get(player.getPlayer().getUniqueId());
    }

    private final String arenaId;
    private final WoolPlayer player;
    private final Menu menu;

    private final List<TeamData> teams = new ArrayList<>();
    private final List<Location> powerUps = new ArrayList<>();

    private String displayName;
    private String group;
    private Location lobby;

    private int maxPlayersPerTeam = 4;
    private int minPlayers = 8;
    private int winPoints = 3;

    private Location arenaPos1;
    private Location arenaPos2;
    private Location centerPos1;
    private Location centerPos2;

    private TeamData currentTeam;
    private boolean settingCenter;

    public SetupSession(WoolPlayer player, String arenaId) {
        this.player = player;
        this.arenaId = arenaId;
        this.menu = new SetupMenu(player, this);

        sessions.put(player.getUniqueId(), this);
        menu.open();
    }

    public boolean isComplete() {
        return isDisplayNameSet() &&
                isGroupSet() &&
                isLobbySet() &&
                isArenaPos1Set() &&
                isArenaPos2Set() &&
                isCenterPos1Set() &&
                isCenterPos2Set() &&
                teams.size() > 1 &&
                teams.stream().allMatch(TeamData::isComplete);
    }

    public void save() {
        // Create the arena file
        File file = new File(WoolWars.get().getArenaManager().getArenaFolder(), arenaId + ".yml");
        FileUtil.create(file);

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("displayname", displayName);
        config.set("group", group);
        config.set("lobby-location", TextUtil.fromLocation(lobby));
        config.set("max-players-per-team", maxPlayersPerTeam);
        config.set("min-players", minPlayers);
        config.set("win-points", winPoints);
        config.set("arena.pos1", TextUtil.fromLocation(arenaPos1));
        config.set("arena.pos2", TextUtil.fromLocation(arenaPos2));
        config.set("center.pos1", TextUtil.fromLocation(centerPos1));
        config.set("center.pos2", TextUtil.fromLocation(centerPos2));

        for (TeamData data : teams) {
            config.set("teams." + data.getColor().toString() + ".name", data.getName());
            config.set("teams." + data.getColor().toString() + ".spawn-location", TextUtil.fromLocation(data.getSpawn()));
            config.set("teams." + data.getColor().toString() + ".barrier.pos1", TextUtil.fromLocation(data.getBarrierPos1()));
            config.set("teams." + data.getColor().toString() + ".barrier.pos2", TextUtil.fromLocation(data.getBarrierPos2()));
            config.set("teams." + data.getColor().toString() + ".base.pos1", TextUtil.fromLocation(data.getBasePos1()));
            config.set("teams." + data.getColor().toString() + ".base.pos2", TextUtil.fromLocation(data.getBasePos2()));
        }

        List<String> locations = new ArrayList<>();
        for (Location location : powerUps) {
            locations.add(TextUtil.fromLocation(location));
        }
        config.set("powerups", locations);

        try {
            config.save(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // Register the arena
        WoolWars.get().getArenaManager().register(new Arena(arenaId, config));

        player.send("&7Arena &e" + arenaId + " &7successfully created and loaded!");
        player.teleportToLobby();
        remove(player);
    }

    public boolean isDisplayNameSet() {
        return displayName != null;
    }

    public boolean isGroupSet() {
        return group != null;
    }

    public boolean isLobbySet() {
        return lobby != null;
    }

    public boolean isArenaPos1Set() {
        return arenaPos1 != null;
    }

    public boolean isArenaPos2Set() {
        return arenaPos2 != null;
    }

    public boolean isCenterPos1Set() {
        return centerPos1 != null;
    }

    public boolean isCenterPos2Set() {
        return centerPos2 != null;
    }

}
