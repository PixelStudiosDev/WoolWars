package me.cubecrafter.woolwars.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.cubecrafter.woolwars.WoolWars;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Infinity
 * 30-03-2022 / 10:28 AM
 * WoolWars1 / me.cubecrafter.woolwars.database
 */

@Getter
@NoArgsConstructor
public class MongoManager {

    private final FileConfiguration config = WoolWars.getInstance().getFileManager().getConfig();
    private final WoolWars woolWars = WoolWars.getInstance();

    private MongoClient client;
    private MongoDatabase database;

    private String host, username, password, valkDatabase, authDatabase;
    private int port;

    private boolean uriEnabled, authEnabled;
    private String uri;

    private MongoCollection<Document> playerData, attempts;

    public void load(){
        this.loadCredentials();

        Logger.getLogger("org.mongodb.driver").setLevel(Level.OFF);

        if (uriEnabled) {
            woolWars.getLogger().info("Attempting to connect to MongoDB... (URI enabled)");
            client = MongoClients.create(MongoClientSettings.builder()
                    .uuidRepresentation(UuidRepresentation.STANDARD)
                    .applyConnectionString(new ConnectionString(uri))
                    .applicationName("woolwars")
                    .build());

            if (client.getClusterDescription().hasWritableServer()) woolWars.getLogger().info("Successfully connected to MongoDB database!");
        } else {
            woolWars.getLogger().info("Attempting to connect to MongoDB... (URI disabled)");
            client = MongoClients.create(MongoClientSettings.builder()
                    .uuidRepresentation(UuidRepresentation.STANDARD)
                    .credential(MongoCredential.createCredential(username, authDatabase, password.toCharArray()))
                    .applicationName("woolwars")
                    .build());

            if (client.getClusterDescription().hasWritableServer()) woolWars.getLogger().info("Successfully connected to the MongoDB database!");
        }

        database = client.getDatabase(valkDatabase);

        playerData = database.getCollection("playerData");
        attempts = database.getCollection("attempts");
    }

    private void loadCredentials() {
        this.uriEnabled = config.getBoolean("mongo.uri-mode");

        if (uriEnabled) {
            this.uri = config.getString("mongo.uri");
            this.valkDatabase = config.getString("mongo.database");
        } else {
            this.host = config.getString("mongo.host");
            this.port = config.getInt("mongo.port");
            this.authDatabase = config.getString("mongo.auth.database");
            this.authEnabled = config.getBoolean("mongo.auth.enabled");

            if (authEnabled) {
                this.username = config.getString("mongo.auth.username");
                this.password = config.getString("mongo.auth.password");
            }
        }
    }
}
