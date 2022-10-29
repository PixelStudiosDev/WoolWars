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
import me.cubecrafter.woolwars.menu.Menu;
import me.cubecrafter.woolwars.menu.MenuItem;
import me.cubecrafter.woolwars.utils.Utils;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetupMenu extends Menu {

    private final SetupSession session;

    public SetupMenu(Player player, SetupSession session) {
        super(player);
        this.session = session;
    }

    @Override
    public String getTitle() {
        return "● Setup: " + session.getId();
    }

    @Override
    public int getRows() {
        return 4;
    }

    @Override
    public Map<Integer, MenuItem> getItems() {
        addFiller(new ItemBuilder("GRAY_STAINED_GLASS_PANE").setDisplayName("&f").build(), Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 28, 29, 30, 31, 32, 33, 34));
        Map<Integer, MenuItem> items = new HashMap<>();
        items.put(10, new MenuItem(new ItemBuilder("NAME_TAG").setDisplayName("&e&lSet Display Name")
                .setLore("&7Click to set the arena display name.", "", session.isDisplayNameSet() ? "&8Current: &7" + session.getDisplayName() + " &a✔" : "&cNot Set ✘").build(), player).addAction(e -> {
            closeMenu();
            TextUtil.sendTitle(player, 3, "&e&lSet Display Name", "&7Type &cCANCEL &7to exit!");
            Utils.requestInput(player, "&7Enter the arena displayname:").thenAccept(input -> {
                if (!input.equalsIgnoreCase("cancel")) {
                    session.setDisplayName(input);
                }
                openMenu();
            });
        }));
        items.put(11, new MenuItem(new ItemBuilder("OAK_SIGN").setDisplayName("&e&lSet Group")
                .setLore("&7Click to set the arena group.", "", session.isGroupSet() ? "&8Current: &7" + session.getGroup() + " &a✔" : "&cNot Set ✘").build(), player).addAction(e -> {
            closeMenu();
            TextUtil.sendTitle(player, 3, "&e&lSet Group", "&7Type &cCANCEL &7to exit!");
            Utils.requestInput(player, "&7Enter the arena group:").thenAccept(input -> {
                if (!input.equalsIgnoreCase("cancel")) {
                    session.setGroup(input);
                }
                openMenu();
            });
        }));
        items.put(12, new MenuItem(new ItemBuilder("ENDER_PEARL").setDisplayName("&e&lSet Lobby Location")
                .setLore("&7Click to set the lobby in your current location.", "", session.isLobbySet() ? "&8Current: &7" + formatLocation(session.getLobby()) + " &a✔" : "&cNot Set ✘").build(), player).addAction(e -> session.setLobby(player.getLocation())));
        items.put(13, new MenuItem(new ItemBuilder("REDSTONE").setDisplayName("&e&lSet Max Players Per Team")
                .setLore("&7Click to set the max amount of players per team.", "&8(Left-Click: Decrease, Right-Click: Increase)", "", "&8Current: &7" + session.getMaxPlayersPerTeam() + " &a✔").build(), player).addAction(e -> {
                    if (session.getMaxPlayersPerTeam() > 1) {
                        session.setMaxPlayersPerTeam(session.getMaxPlayersPerTeam() - 1);
                    }
        }, ClickType.LEFT, ClickType.SHIFT_LEFT).addAction(e -> {
            session.setMaxPlayersPerTeam(session.getMaxPlayersPerTeam() + 1);
        }, ClickType.RIGHT, ClickType.SHIFT_RIGHT));
        items.put(14, new MenuItem(new ItemBuilder("GLOWSTONE_DUST").setDisplayName("&e&lSet Minimum Players")
                .setLore("&7Click to set the minimum amount of players required to start the game.", "&8(Left-Click: Decrease, Right-Click: Increase)", "", "&8Current: &7" + session.getMinPlayers() + " &a✔").build(), player).addAction(e -> {
            if (session.getMinPlayers() > 2) {
                session.setMinPlayers(session.getMinPlayers() - 1);
            }
        }, ClickType.LEFT, ClickType.SHIFT_LEFT).addAction(e -> {
            session.setMinPlayers(session.getMinPlayers() + 1);
        }, ClickType.RIGHT, ClickType.SHIFT_RIGHT));
        items.put(15, new MenuItem(new ItemBuilder("EXP_BOTTLE").setDisplayName("&e&lSet Win Points")
                .setLore("&7Click to set the number of points to win.", "&8(Left-Click: Decrease, Right-Click: Increase)", "", "&8Current: &7" + session.getWinPoints() + " &a✔").build(), player).addAction(e -> {
            if (session.getWinPoints() > 1) {
                session.setWinPoints(session.getWinPoints() - 1);
            }
        }, ClickType.LEFT, ClickType.SHIFT_LEFT).addAction(e -> {
            session.setWinPoints(session.getWinPoints() + 1);
        }, ClickType.RIGHT, ClickType.SHIFT_RIGHT));
        items.put(16, new MenuItem(new ItemBuilder("END_PORTAL_FRAME").setDisplayName("&e&lSet Arena Region")
                .setLore("&7Click to set the arena region.", "&8(Left-Click: Set Point 1, Right-Click: Set Point 2)", "", "&8Point 1: &7" + (session.isArenaPos1Set() ? formatLocation(session.getArenaPos1()) + " &a✔" : "&cNot Set ✘"), "&8Point 2: &7" + (session.isArenaPos2Set() ? formatLocation(session.getArenaPos2()) + " &a✔" : "&cNot Set ✘")).build(), player).addAction(e -> {
            session.setArenaPos1(player.getLocation());
        }, ClickType.LEFT, ClickType.SHIFT_LEFT).addAction(e -> {
            session.setArenaPos2(player.getLocation());
        }, ClickType.RIGHT, ClickType.SHIFT_RIGHT));
        items.put(19, new MenuItem(new ItemBuilder("WHITE_WOOL").setDisplayName("&e&lSet Center Region")
                .setLore("&7Click to set the center region.", "", "&8Point 1: &7" + (session.isCenterPos1Set() ? formatLocation(session.getCenterPos1()) + " &a✔" : "&cNot Set ✘"), "&8Point 2: &7" + (session.isCenterPos2Set() ? formatLocation(session.getCenterPos2()) + " &a✔" : "&cNot Set ✘")).build(), player).addAction(e -> {
            session.setCenterSetupMode(true);
            TextUtil.sendMessage(player, "{prefix}&7Left-Click on a block to set the first point, Right-Click on a block to set the second point.");
            closeMenu();
        }));
        items.put(20, new MenuItem(new ItemBuilder("EMERALD").setDisplayName("&e&lSet Power-Ups").setLore(getPowerUpsLore()).build(), player).addAction(e -> {
            session.getPowerUps().add(player.getLocation());
        }, ClickType.LEFT, ClickType.SHIFT_LEFT).addAction(e -> {
            session.getPowerUps().clear();
        }, ClickType.RIGHT, ClickType.SHIFT_RIGHT));
        items.put(21, new MenuItem(new ItemBuilder("ANVIL").setDisplayName("&e&lManage Teams").setLore("&7Click to manage the teams!").build(), player).addAction(e -> {
            new TeamListMenu(player, session).openMenu();
        }));
        items.put(27, new MenuItem(new ItemBuilder("BARRIER").setDisplayName("&c&lCancel Setup")
                .setLore("&7Click to cancel the arena setup.", "", "&cThis action is irreversible!").build(), player).addAction(e -> {
            closeMenu();
            session.cancel();
        }));
        items.put(35, new MenuItem(new ItemBuilder("EMERALD_BLOCK").setDisplayName("&a&lCreate Arena")
                .setLore("&7Click to create the arena!", "", session.isValid() ? "&aClick to create! ✔" : "&cComplete the setup first! ✘").build(), player).addAction(e -> {
            if (session.isValid()) {
                closeMenu();
                session.save();
            }
        }));
        return items;
    }

    @Override
    public boolean update() {
        return false;
    }

    private String formatLocation(Location location) {
        return location.getWorld().getName() + ", x: " + location.getBlockX() + ", y: " + location.getBlockY() + ", z: " + location.getBlockZ();
    }

    private List<String> getPowerUpsLore() {
        List<String> lore = new ArrayList<>();
        lore.add("&8Left-Click: Add a Power-Up in your current location");
        lore.add("&8Right-Click: Remove all Power-Ups");
        lore.add("");
        for (int i = 0; i < session.getPowerUps().size(); i++) {
            lore.add("&8" + (i + 1) + ": &7" + formatLocation(session.getPowerUps().get(i)));
        }
        return lore;
    }

}
