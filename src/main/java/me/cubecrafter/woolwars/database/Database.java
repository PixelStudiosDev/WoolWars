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
                "name VARCHAR(255)," +
                "wins INT," +
                "losses INT," +
                "games_played INT," +
                "kills INT," +
                "deaths INT," +
                "wool_placed INT," +
                "blocks_broken INT," +
                "powerups_collected INT," +
                "selected_kit VARCHAR(255)" +
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
                resultSet.next();
                data.setWins(resultSet.getInt("wins"));
                data.setLosses(resultSet.getInt("losses"));
                data.setGamesPlayed(resultSet.getInt("games_played"));
                data.setKills(resultSet.getInt("kills"));
                data.setDeaths(resultSet.getInt("deaths"));
                data.setWoolPlaced(resultSet.getInt("wool_placed"));
                data.setBlocksBroken(resultSet.getInt("blocks_broken"));
                data.setPowerUpsCollected(resultSet.getInt("powerups_collected"));
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
            String sql = "UPDATE player_data SET name = ?, wins = ?, losses = ?, games_played = ?, kills = ?, deaths = ?, wool_placed = ?, blocks_broken = ?, powerups_collected = ?, selected_kit = ? WHERE uuid = ?";
            try (Connection connection = pool.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, Bukkit.getOfflinePlayer(data.getUuid()).getName());
                preparedStatement.setInt(2, data.getWins());
                preparedStatement.setInt(3, data.getLosses());
                preparedStatement.setInt(4, data.getGamesPlayed());
                preparedStatement.setInt(5, data.getKills());
                preparedStatement.setInt(6, data.getDeaths());
                preparedStatement.setInt(7, data.getWoolPlaced());
                preparedStatement.setInt(8, data.getBlocksBroken());
                preparedStatement.setInt(9, data.getPowerUpsCollected());
                preparedStatement.setString(10, data.getSelectedKit());
                preparedStatement.setString(11, data.getUuid().toString());
                preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            String sql = "INSERT INTO player_data (uuid, name, wins, losses, games_played, kills, deaths, wool_placed, blocks_broken, powerups_collected, selected_kit) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
            try (Connection connection = pool.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, data.getUuid().toString());
                preparedStatement.setString(2, Bukkit.getOfflinePlayer(data.getUuid()).getName());
                preparedStatement.setInt(3, data.getWins());
                preparedStatement.setInt(4, data.getLosses());
                preparedStatement.setInt(5, data.getGamesPlayed());
                preparedStatement.setInt(6, data.getKills());
                preparedStatement.setInt(7, data.getDeaths());
                preparedStatement.setInt(8, data.getWoolPlaced());
                preparedStatement.setInt(9, data.getBlocksBroken());
                preparedStatement.setInt(10, data.getPowerUpsCollected());
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
