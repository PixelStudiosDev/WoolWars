package me.cubecrafter.woolwars.core;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Data
public class Kit {

    private final String name;
    private final List<ItemStack> items;

}
