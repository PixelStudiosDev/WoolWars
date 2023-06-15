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

package me.cubecrafter.woolwars.storage.type;

import me.cubecrafter.woolwars.storage.Database;
import me.cubecrafter.woolwars.storage.player.PlayerData;
import me.cubecrafter.woolwars.storage.player.StatisticType;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class MySQL extends Database {

    private final String INSERT_OR_UPDATE = "INSERT INTO player_data (uuid, selected_kit, wins, losses, games_played, kills, deaths, wool_placed, blocks_broken, powerups_collected, win_streak, highest_win_streak, assists) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?) " +
            "ON DUPLICATE KEY UPDATE selected_kit=?, wins=?, losses=?, games_played=?, kills=?, deaths=?, wool_placed=?, blocks_broken=?, powerups_collected=?, win_streak=?, highest_win_streak=?, assists=?";

    @Override
    public void saveData(WoolPlayer player) {
        executeAsync(INSERT_OR_UPDATE, statement -> {
            setPlaceholders(statement, player);
            statement.executeUpdate();
        });
    }

    @Override
    public void saveAllData(Collection<WoolPlayer> players) {
        if (players.isEmpty()) return;
        execute(INSERT_OR_UPDATE, statement -> {
            for (WoolPlayer player : players) {
                setPlaceholders(statement, player);
                statement.addBatch();
            }
            statement.executeBatch();
        });
    }

    private void setPlaceholders(PreparedStatement statement, WoolPlayer player) throws SQLException {
        PlayerData data = player.getData();
        statement.setString(1, player.getUniqueId().toString());

        statement.setString(2, data.getSelectedKit());
        statement.setInt(3, data.getStatistic(StatisticType.WINS));
        statement.setInt(4, data.getStatistic(StatisticType.LOSSES));
        statement.setInt(5, data.getStatistic(StatisticType.GAMES_PLAYED));
        statement.setInt(6, data.getStatistic(StatisticType.KILLS));
        statement.setInt(7, data.getStatistic(StatisticType.DEATHS));
        statement.setInt(8, data.getStatistic(StatisticType.WOOL_PLACED));
        statement.setInt(9, data.getStatistic(StatisticType.BLOCKS_BROKEN));
        statement.setInt(10, data.getStatistic(StatisticType.POWERUPS_COLLECTED));
        statement.setInt(11, data.getStatistic(StatisticType.WIN_STREAK));
        statement.setInt(12, data.getStatistic(StatisticType.HIGHEST_WIN_STREAK));
        statement.setInt(13, data.getStatistic(StatisticType.ASSISTS));

        statement.setString(14, data.getSelectedKit());
        statement.setInt(15, data.getStatistic(StatisticType.WINS));
        statement.setInt(16, data.getStatistic(StatisticType.LOSSES));
        statement.setInt(17, data.getStatistic(StatisticType.GAMES_PLAYED));
        statement.setInt(18, data.getStatistic(StatisticType.KILLS));
        statement.setInt(19, data.getStatistic(StatisticType.DEATHS));
        statement.setInt(20, data.getStatistic(StatisticType.WOOL_PLACED));
        statement.setInt(21, data.getStatistic(StatisticType.BLOCKS_BROKEN));
        statement.setInt(22, data.getStatistic(StatisticType.POWERUPS_COLLECTED));
        statement.setInt(23, data.getStatistic(StatisticType.WIN_STREAK));
        statement.setInt(24, data.getStatistic(StatisticType.HIGHEST_WIN_STREAK));
        statement.setInt(25, data.getStatistic(StatisticType.ASSISTS));
    }

}
