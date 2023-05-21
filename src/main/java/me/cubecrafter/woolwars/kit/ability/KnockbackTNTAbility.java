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

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.metadata.FixedMetadataValue;

public class KnockbackTNTAbility extends Ability {

    public KnockbackTNTAbility(ConfigurationSection section) {
        super(section, Type.KNOCKBACK_TNT);
    }

    @Override
    public boolean handle(WoolPlayer player, Arena arena) {
        TNTPrimed entity = (TNTPrimed) arena.getWorld().spawnEntity(player.getLocation(), EntityType.PRIMED_TNT);
        entity.setMetadata("woolwars", new FixedMetadataValue(WoolWars.get(), section.getDouble("power")));
        entity.setIsIncendiary(false);
        entity.setFuseTicks(20);
        return true;
    }

}
