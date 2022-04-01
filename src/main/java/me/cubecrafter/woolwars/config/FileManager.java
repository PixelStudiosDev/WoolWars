package me.cubecrafter.woolwars.config;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileManager {

    private final File configFile;
    private final File messagesFile;
    private final File kitsFile;
    @Getter private YamlConfiguration config;
    @Getter private YamlConfiguration messages;
    @Getter private YamlConfiguration kits;

    public FileManager() {
        new File(WoolWars.getInstance().getDataFolder(), "arenas").mkdirs();
        configFile = new File(WoolWars.getInstance().getDataFolder(), "config.yml");
        messagesFile = new File(WoolWars.getInstance().getDataFolder(), "messages.yml");
        kitsFile = new File(WoolWars.getInstance().getDataFolder(), "kits.yml");
        if (!configFile.exists()) {
            WoolWars.getInstance().saveResource("config.yml", false);
        }
        if (!messagesFile.exists()) {
            WoolWars.getInstance().saveResource("messages.yml", false);
        }
        if (!kitsFile.exists()) {
            WoolWars.getInstance().saveResource("kits.yml", false);
        }
        reload();
    }

    public void save() {
        try {
            config.save(configFile);
            messages.save(messagesFile);
            kits.save(kitsFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(configFile);
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        kits = YamlConfiguration.loadConfiguration(kitsFile);
    }

    public File[] getArenas() {
        return new File(WoolWars.getInstance().getDataFolder(), "arenas").listFiles((dir, name) -> name.endsWith(".yml"));
    }

}