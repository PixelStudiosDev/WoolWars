package me.cubecrafter.woolwars.config;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileManager {

    private final File configFile;
    private final File messagesFile;
    private final File scoreboardFile;
    @Getter private YamlConfiguration config;
    @Getter private YamlConfiguration scoreboard;
    @Getter private YamlConfiguration messages;

    public FileManager() {
        new File(WoolWars.getInstance().getDataFolder(), "arenas").mkdirs();
        configFile = new File(WoolWars.getInstance().getDataFolder(), "config.yml");
        messagesFile = new File(WoolWars.getInstance().getDataFolder(), "messages.yml");
        scoreboardFile = new File(WoolWars.getInstance().getDataFolder(), "scoreboard.yml");
        if (!configFile.exists()) {
            WoolWars.getInstance().saveResource("config.yml", false);
        }
        if (!messagesFile.exists()) {
            WoolWars.getInstance().saveResource("messages.yml", false);
        }
        if (!scoreboardFile.exists()) {
            WoolWars.getInstance().saveResource("scoreboard.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        scoreboard = YamlConfiguration.loadConfiguration(scoreboardFile);
    }

    public void save() {
        try {
            config.save(configFile);
            messages.save(messagesFile);
            scoreboard.save(scoreboardFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(configFile);
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        scoreboard = YamlConfiguration.loadConfiguration(scoreboardFile);
    }

    public File[] getArenas() {
        return new File(WoolWars.getInstance().getDataFolder(), "arenas").listFiles((dir, name) -> name.endsWith(".yml"));
    }

}