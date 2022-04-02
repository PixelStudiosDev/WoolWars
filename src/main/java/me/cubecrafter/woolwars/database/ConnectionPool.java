package me.cubecrafter.woolwars.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.cubecrafter.woolwars.WoolWars;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {

    private final YamlConfiguration config = WoolWars.getInstance().getFileManager().getConfig();
    private HikariDataSource dataSource;
    private final String host;
    private final String port;
    private final String database;
    private final String username;
    private final String password;
    private final boolean useSSL;

    public ConnectionPool() {
        host = config.getString("mysql.host");
        port = config.getString("mysql.port");
        database = config.getString("mysql.database");
        username = config.getString("mysql.username");
        password = config.getString("mysql.password");
        useSSL = config.getBoolean("mysql.useSSL");
        setup();
    }

    private void setup() {
        HikariConfig hikariConfig = new HikariConfig();
        if (config.getBoolean("mysql.enabled")) {
            hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        } else {
            File database = new File(WoolWars.getInstance().getDataFolder(), "database.db");
            if (!database.exists()) {
                try {
                    database.getParentFile().mkdirs();
                    database.createNewFile();
                } catch(IOException ex) {
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

