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

package me.cubecrafter.woolwars.config;

import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.WoolWars;
import org.bukkit.configuration.ConfigurationSection;

@RequiredArgsConstructor
public enum Abilities {

    GIGAHEAL("default-abilities.gigaheal"),
    KNOCKBACK_TNT("default-abilities.knockback-tnt"),
    STEP_BACK("default-abilities.step-back"),
    SPRINT("default-abilities.sprint"),
    GOLDEN_SHELL("default-abilities.golden-shell"),
    HACK("default-abilities.hack"),
    CUSTOM_ABILITIES("custom-abilities");

    private final String path;

    public ConfigurationSection getAsSection() {
        return WoolWars.getInstance().getFileManager().getAbilities().getConfigurationSection(path);
    }

}
