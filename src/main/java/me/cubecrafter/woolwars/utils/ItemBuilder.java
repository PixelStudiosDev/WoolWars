package me.cubecrafter.woolwars.utils;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public class ItemBuilder {

    private ItemStack item;

    public ItemBuilder(String material) {
        item = XMaterial.matchXMaterial(material).orElse(XMaterial.STONE).parseItem();
    }

    public ItemBuilder setDisplayName(String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(TextUtil.color(TextUtil.format(name)));
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(TextUtil.format(lore));
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setGlowing(boolean glow) {
        if (glow) {
            ItemMeta meta = item.getItemMeta();
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder setTexture(String identifier) {
        if (identifier == null) return this;
        ItemMeta meta = item.getItemMeta();
        item.setItemMeta(SkullUtils.applySkin(meta, identifier));
        return this;
    }

    public ItemBuilder setColor(Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(color);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setNBT(String key, String value) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString(key, value);
        item = nbtItem.getItem();
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder setPotionEffect(PotionEffect effect) {
        if (effect == null) return this;
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.addCustomEffect(effect, true);
        meta.setMainEffect(effect.getType());
        item.setItemMeta(meta);
        return this;
    }

    public ItemStack build(){
        return item;
    }

    public static boolean hasId(ItemStack item, String id) {
        if (item == null || item.getType().equals(Material.AIR)) return false;
        NBTItem nbtItem = new NBTItem(item);
        if (nbtItem.hasKey("woolwars")) {
            return nbtItem.getString("woolwars").equals(id);
        }
        return false;
    }

}
