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
import me.cubecrafter.woolwars.utils.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TeleporterMenu extends Menu {

    private final Arena arena;

    public TeleporterMenu(Player player, Arena arena) {
        super(player);
        this.arena = arena;
    }

    @Override
    public String getTitle() {
        return Menus.TELEPORTER_MENU_TITLE.getAsString();
    }

    @Override
    public int getRows() {
        return arena.getAlivePlayers().size() == 0 ? 1 : (int) Math.ceil(arena.getAlivePlayers().size() / 9.0);
    }

    @Override
    public Map<Integer, MenuItem> getItems() {
        Map<Integer, MenuItem> items = new HashMap<>();
        for (int i = 0; i < arena.getAlivePlayers().size(); i++) {
            Player alive = arena.getAlivePlayers().get(i);
            items.put(i, new MenuItem(generateItem(alive, ItemBuilder.fromConfig(Menus.TELEPORTER_MENU_PLAYER_ITEM.getAsSection()).build()), player).addAction(e -> {
                closeMenu();
                player.teleport(alive);
            }, ClickType.LEFT, ClickType.SHIFT_LEFT).addAction(e -> {
                closeMenu();
                player.setGameMode(GameMode.SPECTATOR);
                player.setSpectatorTarget(alive);
            }, ClickType.RIGHT, ClickType.SHIFT_RIGHT));
        }
        return items;
    }

    private ItemStack generateItem(Player player, ItemStack original) {
        ItemMeta meta = original.getItemMeta();
        if (meta.hasDisplayName()) {
            meta.setDisplayName(meta.getDisplayName().replace("{player}", player.getName()));
        }
        if (meta.hasLore()) {
            meta.setLore(meta.getLore().stream().map(s -> s.replace("{player}", player.getDisplayName())
                    .replace("{player_health_percentage}", String.valueOf((int) (player.getHealth() / 20 * 100)))).collect(Collectors.toList()));
        }
        original.setItemMeta(meta);
        return new ItemBuilder(original).setTexture(player.getName()).build();
    }

    @Override
    public boolean update() {
        return true;
    }

}
