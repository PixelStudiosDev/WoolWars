package me.cubecrafter.woolwars.database;

import me.cubecrafter.woolwars.WoolWars;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class Database {

    private final ConnectionPool pool = new ConnectionPool();

    public Database() {
        String sql = "CREATE TABLE IF NOT EXISTS player_data (" +
                "uuid VARCHAR(36) PRIMARY KEY," +
                "name VARCHAR," +
                "wins BIGINT," +
                "losses BIGINT," +
                "games_played BIGINT," +
                "kills BIGINT," +
                "deaths BIGINT," +
                "placed_wool BIGINT," +
                "broken_blocks BIGINT," +
                "powerups_collected BIGINT," +
                "selected_kit VARCHAR" +
                ")";
        try (Connection connection = pool.getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private boolean hasPlayerData(UUID uuid) {
        String sql = "SELECT * FROM player_data WHERE uuid = ?";
        try (Connection connection = pool.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public PlayerData getPlayerData(UUID uuid) {
        PlayerData data = new PlayerData(uuid);
        if (hasPlayerData(uuid)) {
            String sql = "SELECT * FROM player_data WHERE uuid = ?";
            try (Connection connection = pool.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, uuid.toString());
                ResultSet resultSet = preparedStatement.executeQuery();
                data.setStatistic(StatisticType.WINS, resultSet.getInt("wins"));
                data.setStatistic(StatisticType.LOSSES, resultSet.getInt("losses"));
                data.setStatistic(StatisticType.GAMES_PLAYED, resultSet.getInt("games_played"));
                data.setStatistic(StatisticType.KILLS, resultSet.getInt("kills"));
                data.setStatistic(StatisticType.DEATHS, resultSet.getInt("deaths"));
                data.setStatistic(StatisticType.PLACED_WOOL, resultSet.getInt("placed_wool"));
                data.setStatistic(StatisticType.BROKEN_BLOCKS, resultSet.getInt("broken_blocks"));
                data.setStatistic(StatisticType.POWERUPS_COLLECTED, resultSet.getInt("powerups_collected"));
                data.setSelectedKit(resultSet.getString("selected_kit"));
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return data;
    }

    public void savePlayerDataAsync(PlayerData data) {
        Bukkit.getScheduler().runTaskAsynchronously(WoolWars.getInstance(), () -> savePlayerData(data));
    }

    public void savePlayerData(PlayerData data) {
        if (hasPlayerData(data.getUuid())) {
            String sql = "UPDATE player_data SET name = ?, wins = ?, losses = ?, games_played = ?, kills = ?, deaths = ?, placed_wool = ?, broken_blocks = ?, powerups_collected = ?, selected_kit = ? WHERE uuid = ?";
            try (Connection connection = pool.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, Bukkit.getOfflinePlayer(data.getUuid()).getName());
                preparedStatement.setInt(2, data.getStatistic(StatisticType.WINS));
                preparedStatement.setInt(3, data.getStatistic(StatisticType.LOSSES));
                preparedStatement.setInt(4, data.getStatistic(StatisticType.GAMES_PLAYED));
                preparedStatement.setInt(5, data.getStatistic(StatisticType.KILLS));
                preparedStatement.setInt(6, data.getStatistic(StatisticType.DEATHS));
                preparedStatement.setInt(7, data.getStatistic(StatisticType.PLACED_WOOL));
                preparedStatement.setInt(8, data.getStatistic(StatisticType.BROKEN_BLOCKS));
                preparedStatement.setInt(9, data.getStatistic(StatisticType.POWERUPS_COLLECTED));
                preparedStatement.setString(10, data.getSelectedKit());
                preparedStatement.setString(11, data.getUuid().toString());
                preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            String sql = "INSERT INTO player_data (uuid, name, wins, losses, games_played, kills, deaths, placed_wool, broken_blocks, powerups_collected, selected_kit) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
            try (Connection connection = pool.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, data.getUuid().toString());
                preparedStatement.setString(2, Bukkit.getOfflinePlayer(data.getUuid()).getName());
                preparedStatement.setInt(3, data.getStatistic(StatisticType.WINS));
                preparedStatement.setInt(4, data.getStatistic(StatisticType.LOSSES));
                preparedStatement.setInt(5, data.getStatistic(StatisticType.GAMES_PLAYED));
                preparedStatement.setInt(6, data.getStatistic(StatisticType.KILLS));
                preparedStatement.setInt(7, data.getStatistic(StatisticType.DEATHS));
                preparedStatement.setInt(8, data.getStatistic(StatisticType.PLACED_WOOL));
                preparedStatement.setInt(9, data.getStatistic(StatisticType.BROKEN_BLOCKS));
                preparedStatement.setInt(10, data.getStatistic(StatisticType.POWERUPS_COLLECTED));
                preparedStatement.setString(11, data.getSelectedKit());
                preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void close() {
        pool.closePool();
    }

}
