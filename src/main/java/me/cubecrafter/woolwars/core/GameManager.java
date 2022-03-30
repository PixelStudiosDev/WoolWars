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
        for (File file : WoolWars.getInstance().getFileManager().getArenas()) {
            YamlConfiguration arenaConfig = YamlConfiguration.loadConfiguration(file);
            String id = file.getName().replace(".yml", "");
            Arena arena = new Arena(id, arenaConfig);
            addArena(arena);
            TextUtil.info("Arena " + id + " loaded!");
        }
    }

    public void addArena(Arena arena) {
        arenas.put(arena.getId(), arena);
    }

    public void removeArena(String id) {
        arenas.remove(id);
    }

    public Arena getArena() {
        return arenas.get("test");
    }

}
