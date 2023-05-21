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

package me.cubecrafter.woolwars.arena.setup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.cubecrafter.woolwars.arena.team.TeamColor;
import org.bukkit.Location;

@Getter
@Setter
@RequiredArgsConstructor
public class TeamData {

    private final TeamColor color;
    private String name;
    private Location spawn;
    private Location basePos1;
    private Location basePos2;
    private Location barrierPos1;
    private Location barrierPos2;

    public boolean isNameSet() {
        return name != null;
    }

    public boolean isSpawnSet() {
        return spawn != null;
    }

    public boolean isBasePos1Set() {
        return basePos1 != null;
    }

    public boolean isBasePos2Set() {
        return basePos2 != null;
    }

    public boolean isBarrierPos1Set() {
        return barrierPos1 != null;
    }

    public boolean isBarrierPos2Set() {
        return barrierPos2 != null;
    }

    public boolean isValid() {
        return isNameSet() && isSpawnSet() && isBasePos1Set() && isBasePos2Set() && isBarrierPos1Set() && isBarrierPos2Set();
    }

}
