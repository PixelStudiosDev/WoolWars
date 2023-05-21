/*
 * Wool Wars
 * Copyright (C) 2023 CubeCrafter Development
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.cubecrafter.woolwars.utils;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import de.tr7zw.changeme.nbtapi.NBT;
import me.cubecrafter.xutils.TextUtil;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemBuilder {

    private final ItemStack item;
    private ItemMeta meta;

    private final Map<String, String> placeholders = new HashMap<>();
    private boolean legacyPotion;

    public ItemBuilder(ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(String material) {
        // Legacy support for splash potions
        if (material.equalsIgnoreCase("SPLASH_POTION") && !XMaterial.SPLASH_POTION.isSupported()) {
            legacyPotion = true;
            this.item = new ItemStack(Material.POTION);
        } else {
            this.item = XMaterial.matchXMaterial(material).orElse(XMaterial.STONE).parseItem();
        }
        this.meta = item.getItemMeta();
    }

    public ItemBuilder setDisplayName(String name) {
        meta.setDisplayName(TextUtil.color(name));
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        meta.setLore(TextUtil.color(lore));
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder setGlow(boolean glow) {
        if (glow) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else {
            meta.removeEnchant(Enchantment.DURABILITY);
            meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    public ItemBuilder setSkullTexture(String texture) {
        SkullUtils.applySkin(meta, texture);
        return this;
    }

    public ItemBuilder addEnchant(String enchantment, int level) {
        Enchantment enchant = XEnchantment.matchXEnchantment(enchantment).orElse(XEnchantment.DURABILITY).getEnchant();
        meta.addEnchant(enchant, level, true);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder setColor(Color color) {
        LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;
        armorMeta.setColor(color);
        return this;
    }

    public ItemBuilder setTag(String value) {
        item.setItemMeta(meta);
        NBT.modify(item, nbt -> {
            nbt.setString("woolwars", value);
        });
        meta = item.getItemMeta();
        return this;
    }

    // Ugly compatibility method to partially support legacy splash potions
    public ItemBuilder addPotionEffect(String type, int duration, int amplifier) {
        XPotion xPotion = XPotion.matchXPotion(type).orElse(XPotion.SPEED);
        if (legacyPotion) {
            new Potion(xPotion.getPotionType(), amplifier, true).apply(item);
        } else {
            PotionMeta potionMeta = (PotionMeta) meta;
            PotionEffect effect = new PotionEffect(xPotion.getPotionEffectType(), duration * 20, amplifier, false, false);
            potionMeta.setBasePotionData(new PotionData(xPotion.getPotionType()));
            potionMeta.addCustomEffect(effect, true);
            potionMeta.setMainEffect(effect.getType());
        }
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        item.setItemMeta(meta);
        VersionUtil.setUnbreakable(item, unbreakable);
        meta = item.getItemMeta();
        return this;
    }

    public ItemBuilder addPlaceholder(String key, String value) {
        placeholders.put(key, value);
        return this;
    }

    public ItemStack build() {
        // Hide attributes and unbreakable by default
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        // Parse placeholders
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String displayName = meta.getDisplayName();
            setDisplayName(displayName.replace(entry.getKey(), entry.getValue()));

            List<String> lore = meta.getLore();
            lore.replaceAll(line -> line.replace(entry.getKey(), entry.getValue()));
            setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemBuilder fromConfig(ConfigurationSection section) {
        ItemBuilder builder = new ItemBuilder(section.getString("material"));
        if (section.contains("displayname")) {
            builder.setDisplayName(section.getString("displayname"));
        }
        if (section.contains("lore")) {
            builder.setLore(section.getStringList("lore"));
        }
        if (section.contains("glow")) {
            builder.setGlow(section.getBoolean("glow"));
        }
        if (section.contains("texture")) {
            builder.setSkullTexture(section.getString("texture"));
        }
        if (section.contains("amount")) {
            builder.setAmount(section.getInt("amount"));
        }
        if (section.contains("effect")) {
            String[] split = section.getString("effect").split(",");
            // Default duration: 10 seconds, default amplifier: 0
            builder.addPotionEffect(split[0], split.length < 2 ? 10 : Integer.parseInt(split[1]), split.length < 3 ? 0 : Integer.parseInt(split[2]));
        }
        if (section.contains("enchantments")) {
            for (String enchantment : section.getStringList("enchantments")) {
                String[] split = enchantment.split(",");
                // Default level: 1
                builder.addEnchant(split[0], split.length < 2 ? 1 : Integer.parseInt(split[1]));
            }
        }
        return builder;
    }

}
