package me.cubecrafter.woolwars.database;

import me.cubecrafter.woolwars.WoolWars;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {

    private final ConnectionPool pool;

    public Database() {
        pool = new ConnectionPool();
        init();
    }

    private void init() {
        Bukkit.getScheduler().runTaskAsynchronously(WoolWars.getInstance(), () -> {
            String sql = "CREATE TABLE IF NOT EXISTS stats (uuid VARCHAR(36) NOT NULL PRIMARY KEY)";
            try (Connection connection = pool.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.executeUpdate();
            } catch (SQLException ignored) {}
        });
    }

    public void close() {
        pool.closePool();
    }

}
