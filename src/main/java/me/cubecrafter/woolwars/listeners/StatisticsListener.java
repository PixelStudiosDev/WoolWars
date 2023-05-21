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

package me.cubecrafter.woolwars.listeners;

import me.cubecrafter.woolwars.api.events.arena.GameEndEvent;
import me.cubecrafter.woolwars.arena.team.Team;
import me.cubecrafter.woolwars.storage.player.StatisticType;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class StatisticsListener implements Listener {

    @EventHandler
    public void onGameEnd(GameEndEvent event) {
        for (WoolPlayer player : event.getWinnerTeam().getMembers()) {
            player.getData().addStatistic(StatisticType.WINS, 1);
            player.getData().addStatistic(StatisticType.WIN_STREAK, 1);
            player.getData().addStatistic(StatisticType.GAMES_PLAYED, 1);
        }
        for (Team team : event.getLoserTeams()) {
            for (WoolPlayer player : team.getMembers()) {
                player.getData().addStatistic(StatisticType.LOSSES, 1);
                player.getData().setStatistic(StatisticType.WIN_STREAK, 0);
                player.getData().addStatistic(StatisticType.GAMES_PLAYED, 1);
            }
        }
    }

}
