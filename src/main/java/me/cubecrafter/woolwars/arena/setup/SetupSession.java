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

import lombok.Getter;
import lombok.Setter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.menu.setup.SetupMenu;
import me.cubecrafter.woolwars.menu.setup.TeamSetupMenu;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.xutils.FileUtil;
import me.cubecrafter.xutils.TextUtil;
import me.cubecrafter.xutils.menu.Menu;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class SetupSession implements Listener {

    private static final Map<UUID, SetupSession> sessions = new HashMap<>();

    public static boolean check(WoolPlayer player) {
        return sessions.containsKey(player.getPlayer().getUniqueId());
    }

    public static void remove(WoolPlayer player) {
        sessions.remove(player.getPlayer().getUniqueId());
    }

    public static SetupSession get(WoolPlayer player) {
        return sessions.get(player.getPlayer().getUniqueId());
    }

    private final WoolPlayer player;
    private final Menu menu;
    @Setter private boolean centerSetupMode = false;
    private final String id;
    @Setter private String displayName;
    @Setter private String group;
    @Setter private Location lobby;
    @Setter private int maxPlayersPerTeam = 4;
    @Setter private int minPlayers = 8;
    @Setter private int winPoints = 3;
    @Setter private Location arenaPos1;
    @Setter private Location arenaPos2;
    @Setter private Location centerPos1;
    @Setter private Location centerPos2;
    @Setter private TeamData currentEditingTeam;
    private final List<TeamData> teams = new ArrayList<>();
    private final List<Location> powerUps = new ArrayList<>();

    public SetupSession(WoolPlayer player, String id) {
        this.player = player;
        this.id = id;
        WoolWars.get().getServer().getPluginManager().registerEvents(this, WoolWars.get());
        sessions.put(player.getPlayer().getUniqueId(), this);
        menu = new SetupMenu(player, this);
        menu.open();
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

    public boolean isValid() {
        return isDisplayNameSet() &&
                isGroupSet() &&
                isLobbySet() &&
                isArenaPos1Set() &&
                isArenaPos2Set() &&
                isCenterPos1Set() &&
                isCenterPos2Set() &&
                teams.size() > 1 &&
                teams.stream().allMatch(TeamData::isValid);
    }

    public void cancel() {
        HandlerList.unregisterAll(this);
        remove(player);
        player.teleportToLobby();
    }

    public void save() {
        player.teleportToLobby();
        HandlerList.unregisterAll(this);
        File file = new File(WoolWars.get().getConfigManager().getArenasFolder(), id + ".yml");
        if (!file.exists()) {
            FileUtil.create(file);
        }
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
        Arena arena = new Arena(id, config);
        WoolWars.get().getArenaManager().register(arena);
        player.send("&7Arena &e" + id + " &7successfully created and loaded!");
        remove(player);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!e.getPlayer().equals(player.getPlayer())) return;
        if (centerSetupMode) {
            if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                e.setCancelled(true);
                centerPos1 = e.getClickedBlock().getLocation();
                player.send("&aCenter position 1 set!");
                player.playSound("ORB_PICKUP");
            } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                e.setCancelled(true);
                centerPos2 = e.getClickedBlock().getLocation();
                player.send("&aCenter position 2 set!");
                player.playSound("ORB_PICKUP");
            }
            if (isCenterPos1Set() && isCenterPos2Set()) {
                centerSetupMode = false;
                menu.open();
            }
        } else if (currentEditingTeam != null) {
            if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                e.setCancelled(true);
                currentEditingTeam.setBarrierPos1(e.getClickedBlock().getLocation());
                player.send("&aBarrier position 1 set!");
                player.playSound("ORB_PICKUP");
            } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                e.setCancelled(true);
                currentEditingTeam.setBarrierPos2(e.getClickedBlock().getLocation());
                player.send("&aBarrier position 2 set!");
                player.playSound("ORB_PICKUP");
            }
            if (currentEditingTeam.isBarrierPos1Set() && currentEditingTeam.isBarrierPos2Set()) {
                new TeamSetupMenu(player, this, currentEditingTeam).open();
                currentEditingTeam = null;
            }
        }
    }

}
