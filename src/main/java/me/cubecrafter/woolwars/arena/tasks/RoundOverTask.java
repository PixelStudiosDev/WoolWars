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

package me.cubecrafter.woolwars.arena.tasks;

import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.powerup.PowerUp;

public class RoundOverTask extends ArenaTask {

    public RoundOverTask(Arena arena) {
        super(arena, Config.ROUND_OVER_DURATION.getAsInt());
    }

    @Override
    public void execute() {}

    @Override
    public void onEnd() {
        arena.setGameState(GameState.PRE_ROUND);
    }

    @Override
    public void onStart() {
        arena.getPowerUps().forEach(PowerUp::remove);
    }

}
