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

package me.cubecrafter.woolwars.config;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.xutils.FileUtil;
import me.cubecrafter.xutils.TextUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Getter
public class ConfigManager {

    private final WoolWars plugin;

    private YamlConfiguration config;
    private YamlConfiguration messages;
    private YamlConfiguration powerUps;
    private YamlConfiguration menus;

    private final File configFile;
    private final File messagesFile;
    private final File powerUpsFile;
    private final File menusFile;

    private final File arenaFolder = new File(WoolWars.get().getDataFolder(), "arenas");
    private final File kitFolder = new File(WoolWars.get().getDataFolder(), "kits");

    public ConfigManager(WoolWars plugin) {
        this.plugin = plugin;

        File dataFolder = plugin.getDataFolder();
        this.configFile = new File(dataFolder, "config.yml");
        this.messagesFile = new File(dataFolder, "messages.yml");
        this.powerUpsFile = new File(dataFolder, "powerups.yml");
        this.menusFile = new File(dataFolder, "menus.yml");

        load(true);
    }

    public void load(boolean updateFiles) {
        arenaFolder.mkdirs();
        // Create config files
        if (!configFile.exists()) saveResource("config.yml", configFile);
        if (!messagesFile.exists()) saveResource("messages.yml", messagesFile);
        if (!powerUpsFile.exists()) saveResource("powerups.yml", powerUpsFile);
        if (!menusFile.exists()) saveResource("menus.yml", menusFile);
        // Create kits
        if (kitFolder.mkdirs()) {
            for (String kit : new String[]{"archer", "assault", "engineer", "golem", "swordsman", "tank"}) {
                saveResource("kits/" + kit + ".yml", new File(kitFolder, kit + ".yml"));
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        powerUps = YamlConfiguration.loadConfiguration(powerUpsFile);
        menus = YamlConfiguration.loadConfiguration(menusFile);

        if (!updateFiles) return;

        Map<YamlConfiguration, File> configs = new HashMap<>();
        configs.put(config, configFile);
        configs.put(messages, messagesFile);
        configs.put(menus, menusFile);

        boolean updated = false;
        for (Map.Entry<YamlConfiguration, File> entry : configs.entrySet()) {
            YamlConfiguration config = entry.getKey();
            InputStream inputStream = plugin.getResource(entry.getValue().getName());
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
            // Loop through all config values and add them to the config if they don't exist
            for (String key : defaultConfig.getKeys(true)) {
                if (config.contains(key)) continue;
                config.set(key, defaultConfig.get(key));
                updated = true;
            }
        }

        if (updated) {
            save();
            TextUtil.info("Config files have been updated!");
        }
    }

    public void save() {
        try {
            config.save(configFile);
            messages.save(messagesFile);
            powerUps.save(powerUpsFile);
            menus.save(menusFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File[] getArenaFiles() {
        return arenaFolder.listFiles((dir, name) -> name.endsWith(".yml"));
    }

    public File[] getKitFiles() {
        return kitFolder.listFiles((dir, name) -> name.endsWith(".yml"));
    }

    private void saveResource(String path, File destination) {
        FileUtil.copy(plugin.getResource(path), destination);
    }

}