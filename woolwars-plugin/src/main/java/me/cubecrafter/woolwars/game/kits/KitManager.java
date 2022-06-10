package me.cubecrafter.woolwars.game.kits;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class KitManager {

    @Getter private final Map<String, Kit> kits = new HashMap<>();

    public KitManager() {
        loadKits();
    }

    public Kit getKit(String id) {
        return kits.get(id);
    }

    private void loadKits() {
        int loaded = 0;
        for (File kitFile : WoolWars.getInstance().getFileManager().getKits()) {
            String id = kitFile.getName().replace(".yml", "");
            YamlConfiguration kitConfig = YamlConfiguration.loadConfiguration(kitFile);
            Kit kit = new Kit(id, kitConfig);
            kits.put(id, kit);
            TextUtil.info("Kit '" + id + "' loaded!");
            loaded++;
        }
        TextUtil.info("Loaded " + loaded + " kits!");
    }

    public void reload() {
        kits.clear();
        loadKits();
    }

}
