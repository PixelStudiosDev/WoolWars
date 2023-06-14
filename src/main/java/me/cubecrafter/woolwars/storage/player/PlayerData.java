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
import lombok.Setter;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class PlayerData {

    private final UUID uuid;

    private final Map<StatisticType, Integer> overallStatistics = new EnumMap<>(StatisticType.class);
    private final Map<StatisticType, Integer> arenaStatistics = new EnumMap<>(StatisticType.class);
    private final Map<StatisticType, Integer> roundStatistics = new EnumMap<>(StatisticType.class);

    @Setter
    private String selectedKit;

    public void addStatistic(StatisticType type, int amount) {
        overallStatistics.merge(type, amount, Integer::sum);
    }

    public void setStatistic(StatisticType type, int amount) {
        overallStatistics.put(type, amount);
    }

    public void addRoundStatistic(StatisticType type, int amount) {
        // Add to round statistics and arena statistics
        roundStatistics.merge(type, amount, Integer::sum);
        arenaStatistics.merge(type, amount, Integer::sum);
        // Add to overall statistics
        addStatistic(type, amount);
    }

    public int getStatistic(StatisticType type) {
        return overallStatistics.getOrDefault(type, 0);
    }

    public int getArenaStatistic(StatisticType type) {
        return arenaStatistics.getOrDefault(type, 0);
    }

    public int getRoundStatistic(StatisticType type) {
        return roundStatistics.getOrDefault(type, 0);
    }

    public void resetArenaStats() {
        arenaStatistics.clear();
    }

    public void resetRoundStats() {
        roundStatistics.clear();
    }

}
