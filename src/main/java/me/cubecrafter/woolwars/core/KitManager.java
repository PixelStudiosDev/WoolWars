package me.cubecrafter.woolwars.core;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitManager {

    public KitManager() {
        YamlConfiguration kitsFile = WoolWars.getInstance().getFileManager().getKits();
        for (String key : kitsFile.getKeys(false)) {
            List<ItemStack> items = new ArrayList<>();
            for (String item : kitsFile.getStringList(key)) {
                items.add(new ItemBuilder(item).build());
            }
            Kit kit = new Kit(key, items);
            kits.put(key, kit);
        };
    }

    private final Map<String, Kit> kits = new HashMap<>();

    public Kit getKit(String name) {
        return kits.get(name);
    }

}
