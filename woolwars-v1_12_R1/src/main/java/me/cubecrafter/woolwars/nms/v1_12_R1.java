package me.cubecrafter.woolwars.nms;

import me.cubecrafter.woolwars.api.nms.NMS;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class v1_12_R1 extends NMS {

    private final Common common;

    public v1_12_R1(Plugin plugin) {
        super(plugin);
        common = new Common(plugin);
    }

    @Override
    public ItemStack setTag(ItemStack item, String key, String value) {
        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound compound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        compound.set(key, new NBTTagString(value));
        nmsItem.setTag(compound);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    @Override
    public String getTag(ItemStack item, String key) {
        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (nmsItem.hasTag()) {
            NBTTagCompound compound = nmsItem.getTag();
            if (compound.hasKey(key)) {
                return compound.getString(key);
            }
        }
        return null;
    }

    @Override
    public void showPlayer(Player player, Player target) {
        player.showPlayer(plugin, target);
    }

    @Override
    public void hidePlayer(Player player, Player target) {
        player.hidePlayer(plugin, target);
    }

    @Override
    public void setUnbreakable(ItemStack item, boolean unbreakable) {
        common.setUnbreakable(item, unbreakable);
    }

}
