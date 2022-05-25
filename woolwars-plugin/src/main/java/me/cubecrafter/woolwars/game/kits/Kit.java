package me.cubecrafter.woolwars.game.kits;

import com.cryptomorin.xseries.XPotion;
import lombok.Getter;
import me.cubecrafter.woolwars.game.team.Team;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Kit {

    private final String id;
    private final String displayName;
    private final boolean helmetEnabled, chestplateEnabled, leggingsEnabled, bootsEnabled;
    private final boolean defaultKit;
    private final Map<ItemStack, Integer> contents = new HashMap<>();
    private final ItemStack menuItem;
    private final Ability ability;
    private final List<PotionEffect> persistentEffects = new ArrayList<>();
    private final double cost;

    public Kit(String id, YamlConfiguration kitConfig) {
        this.id = id;
        this.displayName = TextUtil.color(kitConfig.getString("displayname"));
        this.helmetEnabled = kitConfig.getBoolean("armor.helmet");
        this.chestplateEnabled = kitConfig.getBoolean("armor.chestplate");
        this.leggingsEnabled = kitConfig.getBoolean("armor.leggings");
        this.bootsEnabled = kitConfig.getBoolean("armor.boots");
        this.defaultKit = kitConfig.getBoolean("default-kit");
        this.cost = kitConfig.getDouble("cost");
        this.ability = new Ability(kitConfig);
        String menuItemMaterial = kitConfig.getString("menu-item.material");
        String menuItemDisplayName = kitConfig.getString("menu-item.displayname");
        List<String> menuItemLore = kitConfig.getStringList("menu-item.lore");
        menuItem = new ItemBuilder(menuItemMaterial).setDisplayName(menuItemDisplayName).setLore(menuItemLore).build();
        for (String item : kitConfig.getConfigurationSection("items").getKeys(false)) {
            String material = kitConfig.getString("items." + item + ".material");
            int amount = kitConfig.getInt("items." + item + ".amount");
            String displayName = kitConfig.getString("items." + item + ".displayname");
            List<String> lore = kitConfig.getStringList("items." + item + ".lore");
            int slot = kitConfig.getInt("items." + item + ".slot");
            PotionEffect potionEffect = null;
            if (kitConfig.isSet("items." + item + ".effect") && material.contains("POTION")) {
                potionEffect = TextUtil.getEffect(kitConfig.getString("items." + item + ".effect"));
            }
            ItemStack created = new ItemBuilder(material).setAmount(amount).setDisplayName(displayName).setLore(lore).setPotionEffect(potionEffect).build();
            contents.put(created, slot);
        }
    }

    public void addToPlayer(Player player, Team team) {
        if (team == null) return;
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.sendMessage(TextUtil.color("&7Kit &b" + displayName + " &7selected!"));
        if (helmetEnabled) player.getInventory().setHelmet(new ItemBuilder("LEATHER_HELMET").setColor(team.getTeamColor().getColor()).build());
        if (chestplateEnabled) player.getInventory().setChestplate(new ItemBuilder("LEATHER_CHESTPLATE").setColor(team.getTeamColor().getColor()).build());
        if (leggingsEnabled) player.getInventory().setLeggings(new ItemBuilder("LEATHER_LEGGINGS").setColor(team.getTeamColor().getColor()).build());
        if (bootsEnabled) player.getInventory().setBoots(new ItemBuilder("LEATHER_BOOTS").setColor(team.getTeamColor().getColor()).build());
        for (Map.Entry<ItemStack, Integer> entry : contents.entrySet()) {
            if (entry.getKey().getType().toString().contains("WOOL")) {
                ItemMeta meta = entry.getKey().getItemMeta();
                ItemStack oldWool = entry.getKey();
                ItemStack wool = new ItemBuilder(team.getTeamColor().getWoolMaterial()).setAmount(oldWool.getAmount()).setDisplayName(meta.getDisplayName()).setLore(meta.getLore()).build();
                player.getInventory().setItem(entry.getValue(), wool);
            } else if (entry.getKey().getType().toString().contains("GLASS")) {
                ItemMeta meta = entry.getKey().getItemMeta();
                ItemStack oldGlass = entry.getKey();
                ItemStack glass = new ItemBuilder(team.getTeamColor().getGlassMaterial()).setAmount(oldGlass.getAmount()).setDisplayName(meta.getDisplayName()).setLore(meta.getLore()).build();
                player.getInventory().setItem(entry.getValue(), glass);
            } else {
                player.getInventory().setItem(entry.getValue(), entry.getKey());
            }
        }
        player.getInventory().setItem(8, ability.getItem());
    }

}
