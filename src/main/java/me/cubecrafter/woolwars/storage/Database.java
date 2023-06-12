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

package me.cubecrafter.woolwars.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.storage.player.PlayerData;
import me.cubecrafter.woolwars.storage.player.StatisticType;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.xutils.Tasks;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class Database {

    private final HikariDataSource dataSource;

    public Database() {
        HikariConfig hikariConfig = new HikariConfig();

        if (Config.MYSQL_ENABLED.asBoolean()) {
            hikariConfig.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s",
                    Config.MYSQL_HOST.asString(),
                    Config.MYSQL_PORT.asString(),
                    Config.MYSQL_DATABASE.asString()));
            hikariConfig.setUsername(Config.MYSQL_USERNAME.asString());
            hikariConfig.setPassword(Config.MYSQL_PASSWORD.asString());
        } else {
            File database = new File(WoolWars.get().getDataFolder(), "database.db");
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
        hikariConfig.addDataSourceProperty("useSSL", Config.MYSQL_SSL_ENABLED.asBoolean());
        hikariConfig.addDataSourceProperty("characterEncoding", "utf8");
        hikariConfig.addDataSourceProperty("useUnicode", true);
        hikariConfig.addDataSourceProperty("cachePrepStmts", true);
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", 250);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);

        this.dataSource = new HikariDataSource(hikariConfig);

        createTable();
        updateTable();
    }

    protected void execute(String sql, ThrowingConsumer<PreparedStatement> consumer) {
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            consumer.accept(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void executeAsync(String sql, ThrowingConsumer<PreparedStatement> consumer) {
        Tasks.async(() -> execute(sql, consumer));
    }

    private void createTable() {
        execute("CREATE TABLE IF NOT EXISTS player_data (" +
                "uuid VARCHAR(36) PRIMARY KEY," +
                "selected_kit VARCHAR(255)," +
                "wins INT," +
                "losses INT," +
                "games_played INT," +
                "kills INT," +
                "deaths INT," +
                "wool_placed INT," +
                "blocks_broken INT," +
                "powerups_collected INT," +
                "win_streak INT," +
                "highest_win_streak INT)",
                PreparedStatement::executeUpdate);
    }

    private void updateTable() {
        List<String> columns = new ArrayList<>();
        columns.add("highest_win_streak");
        // Check already existing columns
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getColumns(null, null, "player_data", null);
            while (resultSet.next()) {
                String column = resultSet.getString("COLUMN_NAME");
                columns.removeIf(string -> string.equals(column));
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Add missing columns
        for (String column : columns) {
            execute("ALTER TABLE player_data ADD COLUMN " + column + " INT", PreparedStatement::executeUpdate);
        }
    }

    public CompletableFuture<PlayerData> fetchData(UUID uuid) {
        CompletableFuture<PlayerData> future = new CompletableFuture<>();
        // Execute an async query
        executeAsync("SELECT * FROM player_data WHERE uuid=?", statement -> {
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();
            PlayerData data = new PlayerData();
            if (!resultSet.next()) {
                // Return empty data
                future.complete(data);
                return;
            }
            data.setSelectedKit(resultSet.getString("selected_kit"));
            // Loop through all columns and find valid statistic types
            for (StatisticType type : StatisticType.values()) {
                if (type == StatisticType.DAMAGE) continue;
                data.addStatistic(type, resultSet.getInt(type.getId()));
            }
            future.complete(data);
        });
        return future;
    }

    public void close() {
        dataSource.close();
    }

    public abstract void saveData(WoolPlayer player);
    public abstract void saveAllData(Collection<WoolPlayer> players);

    @FunctionalInterface
    public interface ThrowingConsumer<T> {
        void accept(T t) throws SQLException;
    }

}

