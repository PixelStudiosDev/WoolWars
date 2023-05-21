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

package me.cubecrafter.woolwars.arena;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.config.Messages;

@Getter
@RequiredArgsConstructor
public enum GameState {

    WAITING(Messages.GAME_STATE_WAITING.asString()),
    STARTING(Messages.GAME_STATE_STARTING.asString()),
    PRE_ROUND(Messages.GAME_STATE_PRE_ROUND.asString()),
    ACTIVE_ROUND(Messages.GAME_STATE_ACTIVE_ROUND.asString()),
    ROUND_OVER(Messages.GAME_STATE_ROUND_OVER.asString()),
    GAME_ENDED(Messages.GAME_STATE_GAME_ENDED.asString());

    private final String name;

}
