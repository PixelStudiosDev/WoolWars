package me.cubecrafter.woolwars.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.config.Configuration;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {

    private HikariDataSource dataSource;
    private final String host, port, database, username, password;
    private final boolean useSSL;

    public ConnectionPool() {
        host = Configuration.MYSQL_HOST.getAsString();
        port = Configuration.MYSQL_PORT.getAsString();
        database = Configuration.MYSQL_DATABASE.getAsString();
        username = Configuration.MYSQL_USERNAME.getAsString();
        password = Configuration.MYSQL_PASSWORD.getAsString();
        useSSL = Configuration.MYSQL_SSL_ENABLED.getAsBoolean();
        setup();
    }

    private void setup() {
        HikariConfig hikariConfig = new HikariConfig();
        if (Configuration.MYSQL_ENABLED.getAsBoolean()) {
            hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        } else {
            File database = new File(WoolWars.getInstance().getDataFolder(), "database.db");
            if (!database.exists()) {
                try {
                    database.createNewFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            hikariConfig.setJdbcUrl("jdbc:sqlite:" + database);
            hikariConfig.setDriverClassName("org.sqlite.JDBC");
        }
        hikariConfig.addDataSourceProperty("characterEncoding", "utf8");
        hikariConfig.addDataSourceProperty("useUnicode", "true");
        hikariConfig.addDataSourceProperty("useSSL", String.valueOf(useSSL));
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setConnectionTestQuery("SELECT 1;");
        hikariConfig.setPoolName("WoolWars-Pool");
        dataSource = new HikariDataSource(hikariConfig);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

}

