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

package me.cubecrafter.woolwars.arena;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.xutils.TextUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ArenaManager {

    @Getter
    private final File arenaFolder;

    private final Map<String, Arena> arenas = new HashMap<>();

    public ArenaManager(WoolWars plugin) {
        this.arenaFolder = new File(plugin.getDataFolder(), "arenas");
    }

    public void load() {
        arenaFolder.mkdirs();

        for (File file : getArenaFiles()) {
            String id = file.getName().replace(".yml", "");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            register(new Arena(id, config));
        }
        TextUtil.info("Loaded " + arenas.size() + " arenas!");
    }

    public void register(Arena arena) {
        arenas.put(arena.getId(), arena);
        TextUtil.info("Arena '" + arena.getId() + "' loaded!");
    }

    public Arena getArena(String id) {
        return arenas.get(id);
    }

    public Collection<Arena> getArenas() {
        return arenas.values();
    }

    public void disable() {
        getArenas().forEach(Arena::restart);
    }

    public File[] getArenaFiles() {
        return arenaFolder.listFiles((dir, name) -> name.endsWith(".yml"));
    }

}
