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

package me.cubecrafter.woolwars.kit;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.xutils.TextUtil;
import me.cubecrafter.xutils.config.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class KitManager {

    private final String[] DEFAULT_KITS = new String[] {
            "archer", "assault", "engineer", "golem", "swordsman", "tank"
    };

    @Getter
    private final File folder;
    private final Map<String, Kit> kits = new HashMap<>();

    public KitManager(WoolWars plugin) {
        this.folder = new File(plugin.getDataFolder(), "kits");
    }

    public void load() {
        kits.clear();
        // If the folder doesn't exist, load the default kits
        if (folder.mkdirs()) {
            for (String kit : DEFAULT_KITS) {
                new Configuration("kits/" + kit + ".yml").load();
            }
        }
        // Load all kits
        for (File file : getKitFiles()) {
            String id = file.getName().replace(".yml", "");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            kits.put(id, new Kit(id, config));
        }
        TextUtil.info("Loaded " + kits.size() + " kits!");
    }

    public Collection<Kit> getKits() {
        return kits.values();
    }

    public Kit getKit(String id) {
        return kits.get(id);
    }

    public File[] getKitFiles() {
        return folder.listFiles((dir, name) -> name.endsWith(".yml"));
    }

}
