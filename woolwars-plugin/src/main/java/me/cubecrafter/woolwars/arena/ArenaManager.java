package me.cubecrafter.woolwars.arena;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.api.arena.Arena;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArenaManager {

    private final Map<String, Arena> arenas = new HashMap<>();

    public ArenaManager() {
        loadArenas();
    }

    private void loadArenas() {
        int loaded = 0;
        for (File file : WoolWars.getInstance().getFileManager().getArenaFiles()) {
            registerArena(getArenaFromFile(file));
            loaded++;
        }
        TextUtil.info("Loaded " + loaded + " arenas!");
    }

    public void registerArena(Arena arena) {
        arenas.put(arena.getId(), arena);
        TextUtil.info("Arena '" + arena.getId() + "' loaded!");
    }

    public Arena getArenaFromFile(File file) {
        YamlConfiguration arenaConfig = YamlConfiguration.loadConfiguration(file);
        String id = file.getName().replace(".yml", "");
        return new GameArena(id, arenaConfig);
    }

    public Arena getArena(String name) {
        return arenas.get(name);
    }

    public void disableArenas() {
        getArenas().forEach(Arena::restart);
    }

    public List<Arena> getArenas() {
        return new ArrayList<>(arenas.values());
    }

}
