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

package me.cubecrafter.woolwars.menu.game;

import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.config.Menus;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.woolwars.arena.ArenaUtil;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.xutils.SoundUtil;
import me.cubecrafter.xutils.menu.MenuItem;
import me.cubecrafter.xutils.menu.PaginatedMenu;

import java.util.Iterator;
import java.util.List;

public class ArenasMenu extends PaginatedMenu {

    private final WoolPlayer player;

    public ArenasMenu(WoolPlayer player) {
        super(player.getPlayer());
        this.player = player;
    }

    @Override
    public void update(int page) {
        // Add filler
        if (Menus.ARENAS_MENU_FILLER_ENABLED.asBoolean()) {
            List<Integer> slots = Menus.ARENAS_MENU_FILLER_SLOTS.asIntegerList();
            setItem(new MenuItem(ItemBuilder.fromConfig(Menus.ARENAS_MENU_FILLER.asSection()).build()), slots);
        }
        // Add arenas
        Iterator<Arena> arenas = getArenas(page).iterator();
        for (int slot : Menus.ARENAS_MENU_ARENA_ITEM_SLOTS.asIntegerList()) {
            if (!arenas.hasNext()) break;
            Arena arena = arenas.next();
            setItem(new MenuItem(ItemBuilder.fromConfig(Menus.ARENAS_MENU_ARENA_ITEM.asSection())
                    .setDisplayName(ArenaUtil.parsePlaceholders(player, Menus.ARENAS_MENU_ARENA_ITEM_DISPLAYNAME.asString(), arena))
                    .setLore(ArenaUtil.parsePlaceholders(player, Menus.ARENAS_MENU_ARENA_ITEM_LORE.asStringList(), arena)).build())
                    .addAction(event -> {
                        close();
                        arena.addPlayer(player, true);
                    }), slot);
        }

        setItem(new MenuItem(ItemBuilder.fromConfig(Menus.ARENAS_MENU_CLOSE_ITEM.asSection()).build())
                .addAction(e -> getPlayer().closeInventory()), Menus.ARENAS_MENU_CLOSE_ITEM_SLOT.asInt());


        setItem(new MenuItem(new ItemBuilder("ARROW").build()).addAction(event -> {
            if (previousPage()) {
                SoundUtil.play(getPlayer(), "UI_BUTTON_CLICK");
            }
        }), 45);

        setItem(new MenuItem(new ItemBuilder("ARROW").build()).addAction(event -> {
            if (nextPage()) {
                SoundUtil.play(getPlayer(), "UI_BUTTON_CLICK");
            }
        }), 53);
    }

    @Override
    public int getRows() {
        return Menus.ARENAS_MENU_ROWS.asInt();
    }

    @Override
    public String getTitle() {
        return Menus.ARENAS_MENU_TITLE.asString();
    }

    @Override
    public int getMaxPages() {
        int slotsPerPage = Menus.ARENAS_MENU_ARENA_ITEM_SLOTS.asIntegerList().size();
        return (int) Math.ceil((double) ArenaUtil.getArenas().size() / slotsPerPage);
    }

    public List<Arena> getArenas(int page) {
        int slotsPerPage = Menus.ARENAS_MENU_ARENA_ITEM_SLOTS.asIntegerList().size();
        return ArenaUtil.getArenas().subList(page * slotsPerPage, Math.min((page + 1) * slotsPerPage, ArenaUtil.getArenas().size()));
    }

}
