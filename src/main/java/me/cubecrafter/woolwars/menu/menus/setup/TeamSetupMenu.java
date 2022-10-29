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

package me.cubecrafter.woolwars.menu.menus.setup;

import me.cubecrafter.woolwars.arena.setup.SetupSession;
import me.cubecrafter.woolwars.arena.setup.TeamData;
import me.cubecrafter.woolwars.menu.Menu;
import me.cubecrafter.woolwars.menu.MenuItem;
import me.cubecrafter.woolwars.utils.Utils;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TeamSetupMenu extends Menu {

    private final SetupSession session;
    private final TeamData data;

    public TeamSetupMenu(Player player, SetupSession session, TeamData data) {
        super(player);
        this.session = session;
        this.data = data;
    }

    @Override
    public String getTitle() {
        return "● Team: " + data.getColor().toString();
    }

    @Override
    public int getRows() {
        return 3;
    }

    @Override
    public Map<Integer, MenuItem> getItems() {
        addFiller(new ItemBuilder("GRAY_STAINED_GLASS_PANE").setDisplayName("&f").build(), Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 19, 20, 21, 22, 23, 24, 25));
        Map<Integer, MenuItem> items = new HashMap<>();
        items.put(10, new MenuItem(new ItemBuilder("NAME_TAG").setDisplayName("&e&lSet Team Name")
                .setLore("&7Click to set the team name.", "", data.isNameSet() ? "&8Current: &7" + data.getName() + " &a✔" : "&cNot Set ✘").build(), player).addAction(e -> {
            closeMenu();
            TextUtil.sendTitle(player, 3, "&e&lSet Team Name", "&7Type &cCANCEL &7to exit!");
            Utils.requestInput(player, "&7Enter the team name:").thenAccept(input -> {
                if (!input.equalsIgnoreCase("cancel")) {
                    data.setName(input);
                }
                openMenu();
            });
        }));
        items.put(11, new MenuItem(new ItemBuilder("ENDER_PEARL").setDisplayName("&e&lSet Spawn Location")
                .setLore("&7Click to set the team spawn location.", "", data.isSpawnSet() ? "&8Current: &7" + formatLocation(data.getSpawn()) + " &a✔" : "&cNot Set ✘").build(), player).addAction(e -> data.setSpawn(player.getLocation())));
        items.put(12, new MenuItem(new ItemBuilder("END_PORTAL_FRAME").setDisplayName("&e&lSet Team Base")
                .setLore("&7Click to set the base of the team.", "&8(Left-Click: Set Point 1, Right-Click: Set Point 2)", "", "&8Point 1: &7" + (data.isBasePos1Set() ? formatLocation(data.getBasePos1()) + " &a✔" : "&cNot Set ✘"), "&8Point 2: &7" + (data.isBasePos2Set() ? formatLocation(data.getBasePos2()) + " &a✔" : "&cNot Set ✘")).build(), player).addAction(e -> {
            data.setBasePos1(player.getLocation());
        }, ClickType.LEFT, ClickType.SHIFT_LEFT).addAction(e -> {
            data.setBasePos2(player.getLocation());
        }, ClickType.RIGHT, ClickType.SHIFT_RIGHT));
        items.put(13, new MenuItem(new ItemBuilder("WHITE_WOOL").setDisplayName("&e&lSet Barrier")
                .setLore("&7Click to set the barrier region.", "", "&8Point 1: &7" + (data.isBarrierPos1Set() ? formatLocation(data.getBarrierPos1()) + " &a✔" : "&cNot Set ✘"), "&8Point 2: &7" + (data.isBarrierPos2Set() ? formatLocation(data.getBarrierPos2()) + " &a✔" : "&cNot Set ✘")).build(), player).addAction(e -> {
            session.setCurrentEditingTeam(data);
            TextUtil.sendMessage(player, "{prefix}&7Left-Click on a block to set the first point, Right-Click on a block to set the second point.");
            closeMenu();
        }));
        items.put(18, new MenuItem(new ItemBuilder("ARROW").setDisplayName("&c&lGo Back")
                .setLore("&7To all teams menu.").build(), player).addAction(e -> {
            new TeamListMenu(player, session).openMenu();
        }));
        items.put(26, new MenuItem(new ItemBuilder("ANVIL").setDisplayName("&c&lDelete Team")
                .setLore("&7Click to delete this team.", "", "&cThis action is irreversible!").build(), player).addAction(e -> {
            session.getTeams().remove(data);
            new TeamListMenu(player, session).openMenu();
        }));
        return items;
    }

    private String formatLocation(Location location) {
        return location.getWorld().getName() + ", x: " + location.getBlockX() + ", y: " + location.getBlockY() + ", z: " + location.getBlockZ();
    }

    @Override
    public boolean update() {
        return false;
    }

}
