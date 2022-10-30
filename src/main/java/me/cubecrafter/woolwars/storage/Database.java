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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.config.FileManager;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class Database {

    private final HikariDataSource dataSource;

    public Database() {
        HikariConfig hikariConfig = new HikariConfig();
        if (Config.MYSQL_ENABLED.getAsBoolean()) {
            hikariConfig.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s",
                    Config.MYSQL_HOST.getAsString(),
                    Config.MYSQL_PORT.getAsString(),
                    Config.MYSQL_DATABASE.getAsString()));
            hikariConfig.setUsername(Config.MYSQL_USERNAME.getAsString());
            hikariConfig.setPassword(Config.MYSQL_PASSWORD.getAsString());
        } else {
            File database = new File(FileManager.PLUGIN_FOLDER, "database.db");
            if (!database.exists()) {
                try {
                    database.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            hikariConfig.setJdbcUrl("jdbc:sqlite:" + database);
            hikariConfig.setDriverClassName("org.sqlite.JDBC");
        }
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setPoolName("WoolWars-Pool");
        hikariConfig.addDataSourceProperty("useSSL", Config.MYSQL_SSL_ENABLED.getAsBoolean());
        hikariConfig.addDataSourceProperty("characterEncoding", "utf8");
        hikariConfig.addDataSourceProperty("useUnicode", true);
        hikariConfig.addDataSourceProperty("cachePrepStmts", true);
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", 250);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        dataSource = new HikariDataSource(hikariConfig);
        createTable();
    }

    protected void execute(String sql, ThrowingConsumer<PreparedStatement> consumer) {
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            consumer.accept(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void executeAsync(String sql, ThrowingConsumer<PreparedStatement> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(WoolWars.getInstance(), () -> execute(sql, consumer));
    }

    public void close() {
        dataSource.close();
    }

    private void createTable() {
        execute("CREATE TABLE IF NOT EXISTS player_data (" +
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
                "selected_kit VARCHAR(255)," +
                "win_streak INT)",
                PreparedStatement::executeUpdate);
    }

    public CompletableFuture<PlayerData> fetchData(UUID uuid) {
        CompletableFuture<PlayerData> future = new CompletableFuture<>();
        executeAsync("SELECT * FROM player_data WHERE uuid=?", statement -> {
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                future.complete(null);
                return;
            }
            PlayerData data = new PlayerData(uuid);
            data.setWins(resultSet.getInt("wins"));
            data.setLosses(resultSet.getInt("losses"));
            data.setGamesPlayed(resultSet.getInt("games_played"));
            data.setKills(resultSet.getInt("kills"));
            data.setDeaths(resultSet.getInt("deaths"));
            data.setWoolPlaced(resultSet.getInt("wool_placed"));
            data.setBlocksBroken(resultSet.getInt("blocks_broken"));
            data.setPowerUpsCollected(resultSet.getInt("powerups_collected"));
            data.setSelectedKit(resultSet.getString("selected_kit"));
            data.setWinStreak(resultSet.getInt("win_streak"));
            future.complete(data);
        });
        return future;
    }

    public abstract void saveData(PlayerData data);
    public abstract void saveAllData(Collection<PlayerData> data);

    @FunctionalInterface
    public interface ThrowingConsumer<T> {
        void accept(T t) throws SQLException;
    }

}

