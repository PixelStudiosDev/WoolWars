package me.cubecrafter.woolwars.core;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;

public class GameManager {

    @Getter private final HashMap<String, Arena> arenas = new HashMap<>();

    public GameManager() {
        loadArenas();
    }

    private void loadArenas() {
        int loaded = 0;
        for (File file : WoolWars.getInstance().getFileManager().getArenas()) {
            YamlConfiguration arenaConfig = YamlConfiguration.loadConfiguration(file);
            String id = file.getName().replace(".yml", "");
            Arena arena = new Arena(id, arenaConfig);
            registerArena(arena);
            loaded++;
        }
        TextUtil.info(loaded + " arenas loaded!");
    }

    public void registerArena(Arena arena) {
        arenas.put(arena.getId(), arena);
    }

    public Arena getArena(String name) {
        return arenas.get(name);
    }

    public void unregisterArena(String id) {
        arenas.remove(id);
    }

}
