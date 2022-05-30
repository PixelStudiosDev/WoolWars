package me.cubecrafter.woolwars.game.kits;

import lombok.Getter;
import me.cubecrafter.woolwars.game.team.Team;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

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
    private final int menuSlot;
    private final Ability ability;
    private final List<PotionEffect> persistentEffects = new ArrayList<>();

    public Kit(String id, YamlConfiguration kitConfig) {
        this.id = id;
        this.displayName = TextUtil.color(kitConfig.getString("displayname"));
        this.helmetEnabled = kitConfig.getBoolean("armor.helmet");
        this.chestplateEnabled = kitConfig.getBoolean("armor.chestplate");
        this.leggingsEnabled = kitConfig.getBoolean("armor.leggings");
        this.bootsEnabled = kitConfig.getBoolean("armor.boots");
        this.defaultKit = kitConfig.getBoolean("default-kit");
        this.ability = new Ability(kitConfig);
        this.menuSlot = kitConfig.getInt("menu-item.slot");
        menuItem = ItemBuilder.fromConfig(kitConfig.getConfigurationSection("menu-item")).build();
        for (String section : kitConfig.getConfigurationSection("items").getKeys(false)) {
            ItemStack item = ItemBuilder.fromConfig(kitConfig.getConfigurationSection("items." + section)).build();
            contents.put(item, kitConfig.getInt("items." + section + ".slot"));
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
        player.getInventory().setItem(ability.getSlot(), ability.getItem());
    }

}
