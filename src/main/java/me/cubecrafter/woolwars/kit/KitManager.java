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

import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.xutils.TextUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class KitManager {

    private final WoolWars plugin;
    private final Map<String, Kit> kits = new HashMap<>();

    public void load() {
        kits.clear();
        for (File file : plugin.getConfigManager().getKitFiles()) {
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

    public Kit getRandomKit() {
        return getKits().toArray(new Kit[0])[ThreadLocalRandom.current().nextInt(getKits().size())];
    }

}
