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

package me.cubecrafter.woolwars.menu;

import lombok.Getter;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public class MenuItem {

    private final ItemStack item;
    private final Player player;
    private final Map<Consumer<InventoryClickEvent>, ClickType[]> actions = new HashMap<>();
    private static final ClickType[] defaultClickTypes = new ClickType[]{ClickType.LEFT, ClickType.RIGHT, ClickType.SHIFT_LEFT, ClickType.SHIFT_RIGHT};

    public MenuItem(ItemStack item, Player viewer) {
        this.item = item;
        this.player = viewer;
        ItemMeta meta = item.getItemMeta();
        if (meta.hasDisplayName()) {
            if (player != null) {
                meta.setDisplayName(TextUtil.format(meta.getDisplayName(), player));
            } else {
                meta.setDisplayName(TextUtil.format(meta.getDisplayName()));
            }
        }
        if (meta.hasLore()) {
            if (player != null) {
                meta.setLore(TextUtil.format(meta.getLore(), player));
            } else {
                meta.setLore(TextUtil.format(meta.getLore()));
            }
        }
        item.setItemMeta(meta);
    }

    public MenuItem addAction(Consumer<InventoryClickEvent> action, ClickType... clickTypes) {
        if (clickTypes.length == 0) {
            clickTypes = defaultClickTypes;
        }
        actions.put(action, clickTypes);
        return this;
    }

}
