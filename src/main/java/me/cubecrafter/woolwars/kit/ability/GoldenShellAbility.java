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

package me.cubecrafter.woolwars.kit.ability;

import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.xutils.Tasks;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class GoldenShellAbility extends Ability {

    private final ItemStack[] armor = new ItemStack[] {
            new ItemBuilder("GOLDEN_BOOTS").build(),
            new ItemBuilder("GOLDEN_LEGGINGS").build(),
            new ItemBuilder("GOLDEN_CHESTPLATE").build(),
            new ItemBuilder("GOLDEN_HELMET").build()
    };

    public GoldenShellAbility(ConfigurationSection section) {
        super(section, Type.GOLDEN_SHELL);
    }

    @Override
    public boolean handle(WoolPlayer player, Arena arena) {
        PlayerInventory inventory = player.getPlayer().getInventory();
        // Save previous armor
        ItemStack[] previous = inventory.getArmorContents();
        inventory.setArmorContents(armor);
        // Restore previous armor
        Tasks.later(() -> {
            if (arena.getState() != GameState.ACTIVE_ROUND || !player.isAlive()) {
                return;
            }
            inventory.setArmorContents(previous);
        }, section.getInt("duration") * 20L);
        return true;
    }

}

