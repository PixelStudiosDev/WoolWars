package me.cubecrafter.woolwars.game.arena;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ArenaManager {

    @Getter private final Map<String, Arena> arenas = new HashMap<>();

    public ArenaManager() {
        loadArenas();
    }

    private void loadArenas() {
        int loaded = 0;
        for (File file : WoolWars.getInstance().getFileManager().getArenaFiles()) {
            YamlConfiguration arenaConfig = YamlConfiguration.loadConfiguration(file);
            String id = file.getName().replace(".yml", "");
            Arena arena = new Arena(id, arenaConfig);
            registerArena(arena);
            TextUtil.info("Arena '" + id + "' loaded!");
            loaded++;
        }
        TextUtil.info("Loaded " + loaded + " arenas!");
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

    public void disableArenas() {
        ArenaUtil.getArenas().forEach(Arena::restart);
    }

}
