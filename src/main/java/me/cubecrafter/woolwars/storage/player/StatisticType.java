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

package me.cubecrafter.woolwars.storage.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum StatisticType {

    WINS("wins"),
    LOSSES("losses"),
    GAMES_PLAYED("games_played"),
    KILLS("kills"),
    DEATHS("deaths"),
    WIN_STREAK("win_streak"),
    HIGHEST_WIN_STREAK("highest_win_streak"),
    WOOL_PLACED("wool_placed"),
    BLOCKS_BROKEN("blocks_broken"),
    POWERUPS_COLLECTED("powerups_collected"),
    DAMAGE("damage");

    private final String id;

    public static StatisticType fromId(String id) {
        return Arrays.stream(values()).filter(type -> type.getId().equals(id)).findAny().orElse(null);
    }

}
