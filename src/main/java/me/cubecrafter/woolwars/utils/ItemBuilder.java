package me.cubecrafter.woolwars.utils;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

    private ItemStack item;
    private boolean legacySplashPotion = false;

    public ItemBuilder(String material) {
        if (material.equalsIgnoreCase("SPLASH_POTION") && !XMaterial.SPLASH_POTION.isSupported()) {
            legacySplashPotion = true;
            item = XMaterial.POTION.parseItem();
        } else {
            item = XMaterial.matchXMaterial(material).get().parseItem();
        }
    }

    public ItemBuilder(ItemStack item) {
        this.item = item;
    }

    public ItemBuilder setDisplayName(String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(TextUtil.color(name));
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(TextUtil.color(lore));
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(TextUtil.color(Arrays.asList(lore)));
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setGlow(boolean glow) {
        if (glow) {
            ItemMeta meta = item.getItemMeta();
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder setTexture(String identifier) {
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof SkullMeta)) return this;
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
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString("woolwars", value);
        item = nbtItem.getItem();
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder addPotionEffect(String type, int duration, int amplifier) {
        if (legacySplashPotion) {
            Potion potion = new Potion(XPotion.matchXPotion(type).get().getPotionType(), amplifier);
            potion.setSplash(true);
            potion.apply(item);
        } else {
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            meta.addCustomEffect(new PotionEffect(XPotion.matchXPotion(type).get().getPotionEffectType(), duration, amplifier, false, false), true);
            meta.setMainEffect(XPotion.matchXPotion(type).get().getPotionEffectType());
            item.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        VersionUtil.setUnbreakable(item, unbreakable);
        return this;
    }

    public ItemBuilder addEnchantment(String enchantment, int level) {
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(XEnchantment.matchXEnchantment(enchantment).get().getEnchant(), level, true);
        item.setItemMeta(meta);
        return this;
    }

    public ItemStack build() {
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);
        return item;
    }

    public static String getTag(ItemStack item) {
        if (item == null) return null;
        NBTItem nbtItem = new NBTItem(item);
        return nbtItem.getString("woolwars");
    }

    public static ItemBuilder fromConfig(ConfigurationSection section) {
        ItemBuilder builder = new ItemBuilder(section.getString("material"));
        if (section.contains("displayname")) builder.setDisplayName(section.getString("displayname"));
        if (section.contains("lore")) builder.setLore(section.getStringList("lore"));
        if (section.contains("glow")) builder.setGlow(section.getBoolean("glow"));
        if (section.contains("texture")) builder.setTexture(section.getString("texture"));
        if (section.contains("amount")) builder.setAmount(section.getInt("amount"));
        if (section.contains("effect")) {
            String[] split = section.getString("effect").split(",");
            builder.addPotionEffect(split[0], Integer.parseInt(split.length < 2 ? "10" : split[1]), Integer.parseInt(split.length < 3 ? "0" : split[2]));
        }
        if (section.contains("enchantments")) {
            for (String enchantment : section.getStringList("enchantments")) {
                String[] split = enchantment.split(",");
                builder.addEnchantment(split[0], Integer.parseInt(split.length < 2 ? "1" : split[1]));
            }
        }
        return builder;
    }

}
