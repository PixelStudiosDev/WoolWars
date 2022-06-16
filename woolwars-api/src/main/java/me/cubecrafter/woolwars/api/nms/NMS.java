package me.cubecrafter.woolwars.api.nms;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public abstract class NMS {

    protected final Plugin plugin;
    public NMS(Plugin plugin) {
        this.plugin = plugin;
    }

    public abstract ItemStack setTag(ItemStack item, String key, String value);
    public abstract String getTag(ItemStack item, String key);

    public abstract void showPlayer(Player player, Player target);

    public abstract void hidePlayer(Player player, Player target);

}
