package me.cubecrafter.woolwars.nms;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class Common {

    private final Plugin plugin;

    public Common(Plugin plugin) {
        this.plugin = plugin;
    }

    /*
    public ItemStack setTag(ItemStack item, String key, String value) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(new NamespacedKey(plugin, "woolwars"), PersistentDataType.STRING, value);
        item.setItemMeta(meta);
        return item;
    }

    public String getTag(ItemStack item, String key) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.getOrDefault(new NamespacedKey(plugin, "woolwars"), PersistentDataType.STRING, "");
    }
    */


    public void setUnbreakable(ItemStack item, boolean unbreakable) {
        ItemMeta meta = item.getItemMeta();
        meta.setUnbreakable(unbreakable);
        item.setItemMeta(meta);
    }

}
