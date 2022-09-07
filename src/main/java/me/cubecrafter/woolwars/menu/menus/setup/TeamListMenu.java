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
import me.cubecrafter.woolwars.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

public class TeamListMenu extends Menu {

    private final SetupSession session;

    public TeamListMenu(Player player, SetupSession session) {
        super(player);
        this.session = session;
    }

    @Override
    public String getTitle() {
        return "● Teams: " + session.getId();
    }

    @Override
    public int getRows() {
        return 4;
    }

    @Override
    public Map<Integer, MenuItem> getItems() {
        addFiller(new ItemBuilder("GRAY_STAINED_GLASS_PANE").setDisplayName("&f").build(), Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 28, 29, 30, 31, 32, 33, 34));
        Map<Integer, MenuItem> items = new HashMap<>();
        Iterator<Integer> it = Arrays.asList(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25).iterator();
        for (TeamData data : session.getTeams()) {
            if (!it.hasNext()) break;
            items.put(it.next(), new MenuItem(new ItemBuilder(data.getColor().getWoolMaterial()).setDisplayName(data.getColor().getChatColor().toString() + data.getColor().toString()).setLore("&8Left-Click to open settings.").build(), player).addAction(e -> {
                new TeamSetupMenu(player, session, data).openMenu();
            }));
        }
        items.put(27, new MenuItem(new ItemBuilder("ARROW").setDisplayName("&c&lGo Back")
                .setLore("&7To the main setup menu.").build(), player).addAction(e -> {
            session.getMenu().openMenu();
        }));
        items.put(35, new MenuItem(new ItemBuilder("EMERALD_BLOCK").setDisplayName("&a&lCreate Team")
                .setLore("&7Click to create a new team!").build(), player).addAction(e -> {
            new SelectColorMenu(player, session).openMenu();
        }));
        return items;
    }

    @Override
    public boolean update() {
        return false;
    }

}
