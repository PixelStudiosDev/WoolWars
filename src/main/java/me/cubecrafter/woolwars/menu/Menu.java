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

import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public abstract class Menu implements InventoryHolder {

    protected final Player player;
    private Inventory inventory;
    private BukkitTask updateTask;
    public abstract String getTitle();
    public abstract int getRows();
    public abstract Map<Integer, MenuItem> getItems();
    public abstract boolean update();

    public void openMenu() {
        updateMenu();
        player.openInventory(getInventory());
        if (update()) {
            updateTask = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), () -> {
                if (player.getOpenInventory() == null || !player.isOnline()) {
                    updateTask.cancel();
                }
                updateMenu();
            }, 0L, 20L);
        }
    }

    public void closeMenu() {
        player.closeInventory();
    }

    public void updateMenu() {
        for (Map.Entry<Integer, MenuItem> entry : getItems().entrySet()) {
            getInventory().setItem(entry.getKey(), entry.getValue().getItem());
        }
    }

    public void addFiller(ItemStack filler, List<Integer> slots) {
        slots.forEach(slot -> getInventory().setItem(slot, filler));
    }

    @Override
    public Inventory getInventory() {
        if (inventory == null) inventory = Bukkit.createInventory(this, getRows() * 9, TextUtil.color(getTitle()));
        return inventory;
    }

}
