package me.cubecrafter.woolwars.utils;

import com.cryptomorin.xseries.ReflectionUtils;
import lombok.experimental.UtilityClass;
import me.cubecrafter.woolwars.WoolWars;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@UtilityClass
public class VersionUtil {

    public void setUnbreakable(ItemStack item, boolean unbreakable) {
        ItemMeta meta = item.getItemMeta();
        if (ReflectionUtils.supports(12)) {
            meta.setUnbreakable(unbreakable);
        } else {
            meta.spigot().setUnbreakable(unbreakable);
        }
        item.setItemMeta(meta);
    }

    public void showPlayer(Player player, Player target) {
        if (ReflectionUtils.supports(12)) {
            player.showPlayer(WoolWars.getInstance(), target);
        } else {
            player.showPlayer(target);
        }
    }

    public void hidePlayer(Player player, Player target) {
        if (ReflectionUtils.supports(12)) {
            player.hidePlayer(WoolWars.getInstance(), target);
        } else {
            player.hidePlayer(target);
        }
    }

}
