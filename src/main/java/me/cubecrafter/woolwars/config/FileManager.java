package me.cubecrafter.woolwars.config;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileManager {

    private final File configFile;
    private final File messagesFile;
    @Getter private YamlConfiguration config;
    @Getter private YamlConfiguration messages;

    public FileManager() {
        new File(WoolWars.getInstance().getDataFolder(), "arenas").mkdirs();
        configFile = new File(WoolWars.getInstance().getDataFolder(), "config.yml");
        messagesFile = new File(WoolWars.getInstance().getDataFolder(), "messages.yml");
        File kitsFolder = new File(WoolWars.getInstance().getDataFolder(), "kits");
        if (kitsFolder.mkdirs()) {
            WoolWars.getInstance().saveResource("kits/builder.yml", false);
        }
        if (!configFile.exists()) {
            WoolWars.getInstance().saveResource("config.yml", false);
        }
        if (!messagesFile.exists()) {
            WoolWars.getInstance().saveResource("messages.yml", false);
        }
        reload();
    }

    public void save() {
        try {
            config.save(configFile);
            messages.save(messagesFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(configFile);
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public File[] getArenas() {
        return new File(WoolWars.getInstance().getDataFolder(), "arenas").listFiles((dir, name) -> name.endsWith(".yml"));
    }

    public File[] getKits() {
        return new File(WoolWars.getInstance().getDataFolder(), "kits").listFiles((dir, name) -> name.endsWith(".yml"));
    }

}