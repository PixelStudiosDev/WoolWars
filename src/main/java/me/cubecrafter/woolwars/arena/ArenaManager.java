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

package me.cubecrafter.woolwars.arena;

import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ArenaManager {

    private final WoolWars plugin;

    private final Map<String, Arena> arenas = new HashMap<>();

    public void load() {
        int loaded = 0;
        for (File file : plugin.getFileManager().getArenaFiles()) {
            String id = file.getName().replace(".yml", "");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            Arena arena = new Arena(id, config);
            register(arena);
            loaded++;
        }
        TextUtil.info("Loaded " + loaded + " arenas!");
    }

    public void register(Arena arena) {
        arenas.put(arena.getId(), arena);
        TextUtil.info("Arena '" + arena.getId() + "' loaded!");
    }

    public Arena getArena(String id) {
        return arenas.get(id);
    }

    public void disable() {
        getArenas().forEach(Arena::restart);
    }

    public List<Arena> getArenas() {
        return new ArrayList<>(arenas.values());
    }

}
