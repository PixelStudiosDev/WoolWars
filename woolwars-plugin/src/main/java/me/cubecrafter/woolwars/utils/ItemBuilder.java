package me.cubecrafter.woolwars.utils;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XMaterial;
import me.cubecrafter.woolwars.WoolWars;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
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

    public ItemBuilder setTag(String value) {
        item = WoolWars.getInstance().getNms().setTag(item, "woolwars", value);
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

    public static boolean hasTag(ItemStack item, String key) {
        if (item == null) return false;
        String tagValue = WoolWars.getInstance().getNms().getTag(item, "woolwars");
        if (tagValue == null) return false;
        return tagValue.equals(key);
    }

}
