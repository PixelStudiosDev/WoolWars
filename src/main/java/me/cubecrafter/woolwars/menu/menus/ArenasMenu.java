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

import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.config.Menus;
import me.cubecrafter.woolwars.menu.Menu;
import me.cubecrafter.woolwars.menu.MenuItem;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.entity.Player;

import java.util.*;

public class ArenasMenu extends Menu {

    public ArenasMenu(Player player) {
        super(player);
    }

    @Override
    public String getTitle() {
        return Menus.ARENAS_MENU_TITLE.getAsString();
    }

    @Override
    public int getRows() {
        return Menus.ARENAS_MENU_ROWS.getAsInt();
    }

    @Override
    public Map<Integer, MenuItem> getItems() {
        if (Menus.ARENAS_MENU_FILLER_ENABLED.getAsBoolean()) {
            addFiller(ItemBuilder.fromConfig(Menus.ARENAS_MENU_FILLER.getAsSection()).build(), Menus.ARENAS_MENU_FILLER_SLOTS.getAsIntegerList());
        }
        Map<Integer, MenuItem> items = new HashMap<>();
        Iterator<Integer> index = Menus.ARENAS_MENU_ARENA_ITEM_SLOTS.getAsIntegerList().iterator();
        for (Arena arena : ArenaUtil.getArenas()) {
            if (!index.hasNext()) break;
            items.put(index.next(), new MenuItem(ItemBuilder.fromConfig(Menus.ARENAS_MENU_ARENA_ITEM.getAsSection())
                    .setDisplayName(TextUtil.format(Menus.ARENAS_MENU_ARENA_ITEM_DISPLAYNAME.getAsString(), arena, player))
                    .setLore(TextUtil.format(Menus.ARENAS_MENU_ARENA_ITEM_LORE.getAsStringList(), arena, player)).build(), player)
                    .addAction(e -> {
                        closeMenu();
                        arena.addPlayer(player);
                    }));
            }
        items.put(Menus.ARENAS_MENU_CLOSE_ITEM_SLOT.getAsInt(), new MenuItem(ItemBuilder.fromConfig(Menus.ARENAS_MENU_CLOSE_ITEM.getAsSection()).build(), player)
                .addAction(e -> closeMenu()));
        return items;
    }

    @Override
    public boolean update() {
        return true;
    }

}
