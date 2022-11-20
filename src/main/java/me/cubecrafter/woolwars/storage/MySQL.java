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

import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class MySQL extends Database {

    private final String INSERT_OR_UPDATE = "INSERT INTO player_data (uuid, name, wins, losses, games_played, kills, deaths, wool_placed, blocks_broken, powerups_collected, selected_kit, win_streak, highest_win_streak) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?) " +
            "ON DUPLICATE KEY UPDATE name=?, wins=?, losses=?, games_played=?, kills=?, deaths=?, wool_placed=?, blocks_broken=?, powerups_collected=?, selected_kit=?, win_streak=?, highest_win_streak=?";

    @Override
    public void saveData(PlayerData data) {
        executeAsync(INSERT_OR_UPDATE, statement -> {
            setPlaceholders(statement, data);
            statement.executeUpdate();
        });
    }

    @Override
    public void saveAllData(Collection<PlayerData> data) {
        if (data.isEmpty()) return;
        execute(INSERT_OR_UPDATE, statement -> {
            for (PlayerData playerData : data) {
                setPlaceholders(statement, playerData);
                statement.addBatch();
            }
            statement.executeBatch();
        });
    }

    private void setPlaceholders(PreparedStatement statement, PlayerData data) throws SQLException {
        String name = Bukkit.getOfflinePlayer(data.getUuid()).getName();
        statement.setString(1, data.getUuid().toString());
        statement.setString(2, name);
        statement.setInt(3, data.getWins());
        statement.setInt(4, data.getLosses());
        statement.setInt(5, data.getGamesPlayed());
        statement.setInt(6, data.getKills());
        statement.setInt(7, data.getDeaths());
        statement.setInt(8, data.getWoolPlaced());
        statement.setInt(9, data.getBlocksBroken());
        statement.setInt(10, data.getPowerUpsCollected());
        statement.setString(11, data.getSelectedKit());
        statement.setInt(12, data.getWinStreak());
        statement.setInt(13, data.getHighestWinStreak());
        statement.setString(14, name);
        statement.setInt(15, data.getWins());
        statement.setInt(16, data.getLosses());
        statement.setInt(17, data.getGamesPlayed());
        statement.setInt(18, data.getKills());
        statement.setInt(19, data.getDeaths());
        statement.setInt(20, data.getWoolPlaced());
        statement.setInt(21, data.getBlocksBroken());
        statement.setInt(22, data.getPowerUpsCollected());
        statement.setString(23, data.getSelectedKit());
        statement.setInt(24, data.getWinStreak());
        statement.setInt(25, data.getHighestWinStreak());
    }

}
