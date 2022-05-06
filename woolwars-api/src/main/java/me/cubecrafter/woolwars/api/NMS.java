package me.cubecrafter.woolwars.api;

import org.bukkit.inventory.ItemStack;

public interface NMS {

    ItemStack setTag(ItemStack item, String key, String value);
    String getTag(ItemStack item, String key);

}
