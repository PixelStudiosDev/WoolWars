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

package me.cubecrafter.woolwars.kits.ability;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public abstract class Ability {

    protected final ConfigurationSection section;
    private final String id;
    private final String displayName;
    private final ItemStack item;

    public Ability(ConfigurationSection section) {
        this.section = section;
        id = section.getName();
        displayName = section.getString("displayname");
        item = ItemBuilder.fromConfig(section.getConfigurationSection("item")).setTag("ability-item").build();
    }

    public void use(Player player, Arena arena) {
        if (arena.getGameState() != GameState.ACTIVE_ROUND) {
            TextUtil.sendMessage(player, Messages.ABILITY_CANT_USE.getAsString());
            return;
        }
        if (WoolWars.getInstance().getKitManager().hasCooldown(player)) {
            TextUtil.sendMessage(player, Messages.ABILITY_ALREADY_USED.getAsString());
            return;
        }
        execute(player, arena);
    }

    public abstract void execute(Player player, Arena arena);

}
