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

package me.cubecrafter.woolwars.config;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class FileManager {

    private final WoolWars plugin;

    @Getter private YamlConfiguration config;
    @Getter private YamlConfiguration messages;
    @Getter private YamlConfiguration powerUps;
    @Getter private YamlConfiguration menus;
    @Getter private YamlConfiguration abilities;

    private final File configFile;
    private final File messagesFile;
    private final File powerUpsFile;
    private final File menusFile;
    private final File abilitiesFile;

    public static final File PLUGIN_FOLDER = WoolWars.getInstance().getDataFolder();
    public static final File ARENAS_FOLDER = new File(PLUGIN_FOLDER, "arenas");
    public static final File KITS_FOLDER = new File(PLUGIN_FOLDER, "kits");

    public FileManager(WoolWars plugin) {
        this.plugin = plugin;
        configFile = new File(PLUGIN_FOLDER, "config.yml");
        messagesFile = new File(PLUGIN_FOLDER, "messages.yml");
        powerUpsFile = new File(PLUGIN_FOLDER, "powerups.yml");
        menusFile = new File(PLUGIN_FOLDER, "menus.yml");
        abilitiesFile = new File(PLUGIN_FOLDER, "abilities.yml");
        load(true);
    }

    public void load(boolean updateFiles) {
        ARENAS_FOLDER.mkdirs();
        if (!configFile.exists()) saveResource("config.yml", configFile);
        if (!messagesFile.exists()) saveResource("messages.yml", messagesFile);
        if (!powerUpsFile.exists()) saveResource("powerups.yml", powerUpsFile);
        if (!menusFile.exists()) saveResource("menus.yml", menusFile);
        if (!abilitiesFile.exists()) saveResource("abilities.yml", abilitiesFile);
        if (KITS_FOLDER.mkdirs()) {
            for (String kit : new String[]{"archer", "assault", "engineer", "golem", "swordsman", "tank"}) {
                saveResource("kits/" + kit + ".yml", new File(KITS_FOLDER, kit + ".yml"));
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        powerUps = YamlConfiguration.loadConfiguration(powerUpsFile);
        menus = YamlConfiguration.loadConfiguration(menusFile);
        abilities = YamlConfiguration.loadConfiguration(abilitiesFile);
        if (!updateFiles) return;
        Map<YamlConfiguration, File> configs = new HashMap<>();
        configs.put(config, configFile);
        configs.put(messages, messagesFile);
        configs.put(menus, menusFile);
        boolean update = false;
        for (Map.Entry<YamlConfiguration, File> entry : configs.entrySet()) {
            YamlConfiguration config = entry.getKey();
            InputStream inputStream = plugin.getResource(entry.getValue().getName());
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
            for (String key : defaultConfig.getKeys(true)) {
                if (config.contains(key)) continue;
                config.set(key, defaultConfig.get(key));
                update = true;
            }
        }
        if (!update) return;
        save();
        load(false);
    }

    public void save() {
        try {
            config.save(configFile);
            messages.save(messagesFile);
            powerUps.save(powerUpsFile);
            menus.save(menusFile);
            abilities.save(abilitiesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveResource(String name, File destination) {
        try {
            Files.copy(plugin.getResource(name), destination.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File[] getArenaFiles() {
        return ARENAS_FOLDER.listFiles((dir, name) -> name.endsWith(".yml"));
    }

    public File[] getKitFiles() {
        return KITS_FOLDER.listFiles((dir, name) -> name.endsWith(".yml"));
    }

}