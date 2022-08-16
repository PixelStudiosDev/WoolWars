package me.cubecrafter.woolwars.config;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class FileManager {

    private final WoolWars plugin;
    private final File configFile;
    private final File messagesFile;
    private final File powerUpsFile;
    private final File menusFile;
    @Getter private YamlConfiguration config;
    @Getter private YamlConfiguration messages;
    @Getter private YamlConfiguration powerUps;
    @Getter private YamlConfiguration menus;

    public FileManager(WoolWars plugin) {
        this.plugin = plugin;
        configFile = new File(plugin.getDataFolder(), "config.yml");
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        powerUpsFile = new File(plugin.getDataFolder(), "powerups.yml");
        menusFile = new File(plugin.getDataFolder(), "menus.yml");
        reload();
    }

    private void createFiles() {
        getArenasDir().mkdirs();
        if (new File(plugin.getDataFolder(), "kits").mkdirs()) {
            List<String> defaultKits = Arrays.asList("archer", "assault", "engineer", "golem", "swordsman", "tank");
            defaultKits.forEach(kit -> saveResource("kits/" + kit + ".yml", new File(plugin.getDataFolder(), "kits/" + kit + ".yml")));
        }
        if (!configFile.exists()) {
            saveResource("config.yml", configFile);
        }
        if (!messagesFile.exists()) {
            saveResource("messages.yml", messagesFile);
        }
        if (!powerUpsFile.exists()) {
            saveResource("powerups.yml", powerUpsFile);
        }
        if (!menusFile.exists()) {
            saveResource("menus.yml", menusFile);
        }
    }

    private void saveResource(String name, File destination) {
        try {
            Files.copy(plugin.getResource(name), destination.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            config.save(configFile);
            messages.save(messagesFile);
            powerUps.save(powerUpsFile);
            menus.save(menusFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void reload() {
        createFiles();
        config = YamlConfiguration.loadConfiguration(configFile);
        TextUtil.info("File 'config.yml' loaded!");
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        TextUtil.info("File 'messages.yml' loaded!");
        powerUps = YamlConfiguration.loadConfiguration(powerUpsFile);
        TextUtil.info("File 'powerups.yml' loaded!");
        menus = YamlConfiguration.loadConfiguration(menusFile);
        TextUtil.info("File 'menus.yml' loaded!");
    }

    public File[] getArenaFiles() {
        return getArenasDir().listFiles((dir, name) -> name.endsWith(".yml"));
    }

    public File[] getKitFiles() {
        return new File(plugin.getDataFolder(), "kits").listFiles((dir, name) -> name.endsWith(".yml"));
    }

    public static File getArenasDir() {
        return new File(WoolWars.getInstance().getDataFolder(), "arenas");
    }

}