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
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

public class StepBackAbility extends Ability {

    public StepBackAbility(ConfigurationSection section) {
        super(section, Type.STEP_BACK);
    }

    @Override
    public boolean handle(WoolPlayer player, Arena arena) {
        Vector velocity = player.getPlayer().getLocation().getDirection().multiply(-section.getDouble("power"));
        player.getPlayer().setVelocity(velocity);
        player.playSound("ENTITY_ENDERMAN_TELEPORT");
        return true;
    }

}

