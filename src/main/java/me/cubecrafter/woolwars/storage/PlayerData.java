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

package me.cubecrafter.woolwars.storage;

import lombok.Data;

import java.util.UUID;

@Data
public class PlayerData {

    private final UUID uuid;
    private int wins;
    private int losses;
    private int gamesPlayed;
    private int kills;
    private int deaths;
    private int winStreak;
    private int highestWinStreak;
    private int woolPlaced;
    private int blocksBroken;
    private int powerUpsCollected;
    private String selectedKit;

}
