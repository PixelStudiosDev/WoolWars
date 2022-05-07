package me.cubecrafter.woolwars.nms;

import me.cubecrafter.woolwars.api.NMS;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class v1_8_R3 implements NMS {

    @Override
    public ItemStack setTag(ItemStack item, String key, String value) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound compound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        compound.set(key, new NBTTagString(value));
        nmsItem.setTag(compound);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    @Override
    public String getTag(ItemStack item, String key) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (nmsItem.hasTag()) {
            NBTTagCompound compound = nmsItem.getTag();
            if (compound.hasKey(key)) {
                return compound.getString(key);
            }
        }
        return null;
    }

}
