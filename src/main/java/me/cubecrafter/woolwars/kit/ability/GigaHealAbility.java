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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GigaHealAbility extends Ability {

    public GigaHealAbility(ConfigurationSection section) {
        super(section, Type.GIGAHEAL);
    }

    @Override
    public boolean handle(WoolPlayer player, Arena arena) {
        PotionEffect effect = new PotionEffect(
                PotionEffectType.REGENERATION,
                section.getInt("effect.duration") * 20,
                section.getInt("effect.amplifier")
        );
        player.getPlayer().addPotionEffect(effect);
        return true;
    }

}

