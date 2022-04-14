package me.cubecrafter.woolwars.kits;

import org.bukkit.inventory.ItemStack;

/**
 * @author Infinity
 * 14-04-2022 / 04:48 PM
 * WoolWars / me.cubecrafter.woolwars.kits
 */

public class Kit {

    private String displayName;
    private ItemStack[] contents;

    public Kit(String displayName, ItemStack[] contents){
        this.displayName = displayName;
        this.contents = contents;
    }

}
