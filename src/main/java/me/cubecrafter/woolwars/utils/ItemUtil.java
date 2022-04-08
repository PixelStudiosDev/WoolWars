package me.cubecrafter.woolwars.utils;

import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class ItemUtil {

    public boolean hasId(ItemStack item, String id) {
        NBTItem nbtItem = new NBTItem(item);
        if (nbtItem.hasKey("id")) {
            return nbtItem.getString("id").equals(id);
        }
        return false;
    }

}
