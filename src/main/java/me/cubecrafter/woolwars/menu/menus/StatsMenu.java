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

package me.cubecrafter.woolwars.menu.menus;

import me.cubecrafter.woolwars.config.Menus;
import me.cubecrafter.woolwars.menu.Menu;
import me.cubecrafter.woolwars.menu.MenuItem;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class StatsMenu extends Menu {

    public StatsMenu(Player player) {
        super(player);
    }

    @Override
    public String getTitle() {
        return Menus.STATS_MENU_TITLE.getAsString();
    }

    @Override
    public int getRows() {
        return Menus.STATS_MENU_ROWS.getAsInt();
    }

    @Override
    public Map<Integer, MenuItem> getItems() {
        if (Menus.STATS_MENU_FILLER_ENABLED.getAsBoolean()) {
            addFiller(ItemBuilder.fromConfig(Menus.STATS_MENU_FILLER.getAsSection()).build(), Menus.STATS_MENU_FILLER_SLOTS.getAsIntegerList());
        }
        Map<Integer, MenuItem> items = new HashMap<>();
        items.put(Menus.STATS_MENU_WINS_ITEM_SLOT.getAsInt(), new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_WINS_ITEM.getAsSection()).build(), player));
        items.put(Menus.STATS_MENU_LOSSES_ITEM_SLOT.getAsInt(), new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_LOSSES_ITEM.getAsSection()).build(), player));
        items.put(Menus.STATS_MENU_GAMES_PLAYED_ITEM_SLOT.getAsInt(), new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_GAMES_PLAYED_ITEM.getAsSection()).build(), player));
        items.put(Menus.STATS_MENU_KILLS_ITEM_SLOT.getAsInt(), new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_KILLS_ITEM.getAsSection()).build(), player));
        items.put(Menus.STATS_MENU_DEATHS_ITEM_SLOT.getAsInt(), new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_DEATHS_ITEM.getAsSection()).build(), player));
        items.put(Menus.STATS_MENU_WOOL_PLACED_ITEM_SLOT.getAsInt(), new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_WOOL_PLACED_ITEM.getAsSection()).build(), player));
        items.put(Menus.STATS_MENU_BLOCKS_BROKEN_ITEM_SLOT.getAsInt(), new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_BLOCKS_BROKEN_ITEM.getAsSection()).build(), player));
        items.put(Menus.STATS_MENU_POWERUPS_COLLECTED_ITEM_SLOT.getAsInt(), new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_POWERUPS_COLLECTED_ITEM.getAsSection()).build(), player));
        items.put(Menus.STATS_MENU_CLOSE_ITEM_SLOT.getAsInt(), new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_CLOSE_ITEM.getAsSection()).build(), player)
                .addAction((e) -> closeMenu()));
        return items;
    }

    @Override
    public boolean update() {
        return false;
    }

}
