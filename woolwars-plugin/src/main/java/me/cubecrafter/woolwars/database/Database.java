package me.cubecrafter.woolwars.database;

import me.cubecrafter.woolwars.WoolWars;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Database {

    private final ConnectionPool pool = new ConnectionPool();

    public Database() {
        createTable();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS player_data (uuid VARCHAR(36) NOT NULL PRIMARY KEY, name VARCHAR(100), wins INT(100), losses INT(100), games_played INT(100), kills INT(100), deaths INT(100), placed_blocks INT(100), broken_blocks INT(100), selected_kit VARCHAR(100))";
        try (Connection connection = pool.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ignored) {}
    }

    private boolean hasPlayerData(UUID uuid) {
        String sql = "SELECT * FROM player_data WHERE uuid = ?";
        try (Connection connection = pool.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException ignored) {}
        return false;
    }

    public PlayerData getPlayerData(UUID uuid) {
        PlayerData data = new PlayerData(uuid);
        if (hasPlayerData(uuid)) {
            String sql = "SELECT * FROM player_data WHERE uuid = ?";
            try (Connection connection = pool.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, uuid.toString());
                ResultSet resultSet = preparedStatement.executeQuery();
                data.setWins(resultSet.getInt("wins"));
                data.setLosses(resultSet.getInt("losses"));
                data.setGamesPlayed(resultSet.getInt("games_played"));
                data.setKills(resultSet.getInt("kills"));
                data.setDeaths(resultSet.getInt("deaths"));
                data.setPlacedBlocks(resultSet.getInt("placed_blocks"));
                data.setBrokenBlocks(resultSet.getInt("broken_blocks"));
                data.setSelectedKit(resultSet.getString("selected_kit"));
            } catch (SQLException ignored) {}
        }
        return data;
    }

    public void savePlayerDataAsync(PlayerData data) {
        Bukkit.getScheduler().runTaskAsynchronously(WoolWars.getInstance(), () -> savePlayerData(data));
    }

    public void savePlayerData(PlayerData data) {
        String update = "UPDATE player_data SET name = ?, wins = ?, losses = ?, games_played = ?, kills = ?, deaths = ?, placed_blocks = ?, broken_blocks = ?, selected_kit = ? WHERE uuid = ?";
        String insert = "INSERT INTO player_data (uuid, name, wins, losses, games_played, kills, deaths, placed_blocks, broken_blocks, selected_kit) VALUES (?,?,?,?,?,?,?,?,?,?)";
        if (hasPlayerData(data.getUuid())) {
            try (Connection connection = pool.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(update)) {
                preparedStatement.setString(1, Bukkit.getOfflinePlayer(data.getUuid()).getName());
                preparedStatement.setInt(2, data.getWins());
                preparedStatement.setInt(3, data.getLosses());
                preparedStatement.setInt(4, data.getGamesPlayed());
                preparedStatement.setInt(5, data.getKills());
                preparedStatement.setInt(6, data.getDeaths());
                preparedStatement.setInt(7, data.getPlacedBlocks());
                preparedStatement.setInt(8, data.getBrokenBlocks());
                preparedStatement.setString(9, data.getSelectedKit());
                preparedStatement.setString(10, data.getUuid().toString());
                preparedStatement.executeUpdate();
            } catch (SQLException ignored) {}
        } else {
            try (Connection connection = pool.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(insert)) {
                preparedStatement.setString(1, data.getUuid().toString());
                preparedStatement.setString(2, Bukkit.getOfflinePlayer(data.getUuid()).getName());
                preparedStatement.setInt(3, data.getWins());
                preparedStatement.setInt(4, data.getLosses());
                preparedStatement.setInt(5, data.getGamesPlayed());
                preparedStatement.setInt(6, data.getKills());
                preparedStatement.setInt(7, data.getDeaths());
                preparedStatement.setInt(8, data.getPlacedBlocks());
                preparedStatement.setInt(9, data.getBrokenBlocks());
                preparedStatement.setString(10, data.getSelectedKit());
                preparedStatement.executeUpdate();
            } catch (SQLException ignored) {}
        }
    }

    public void close() {
        pool.closePool();
    }

}
