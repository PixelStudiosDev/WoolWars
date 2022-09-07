package me.cubecrafter.woolwars.kits;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.kits.ability.Ability;
import me.cubecrafter.woolwars.team.Team;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Kit {

    private final String id;
    private final String displayName;
    private final ItemStack[] armor = new ItemStack[4];
    private final Map<Integer, ItemStack> contents = new HashMap<>();
    private final Ability ability;
    private final int abilitySlot;

    public Kit(String id, YamlConfiguration kitConfig) {
        this.id = id;
        this.displayName = TextUtil.color(kitConfig.getString("displayname"));
        this.ability = WoolWars.getInstance().getKitManager().getAbility(kitConfig.getString("ability.type"));
        this.abilitySlot = kitConfig.getInt("ability.slot");
        for (String section : kitConfig.getConfigurationSection("items").getKeys(false)) {
            ItemStack item = ItemBuilder.fromConfig(kitConfig.getConfigurationSection("items." + section)).setUnbreakable(true).build();
            contents.put(kitConfig.getInt("items." + section + ".slot"), item);
        }
        List<String> armorParts = Arrays.asList("boots", "leggings", "chestplate", "helmet");
        for (int i = 0; i < armorParts.size(); i++) {
            String part = armorParts.get(i);
            ConfigurationSection section = kitConfig.getConfigurationSection("armor." + part);
            if (section == null) continue;
            armor[i] = ItemBuilder.fromConfig(section).setUnbreakable(true).build();
        }
    }

    public void addToPlayer(Player player, Team team) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        TextUtil.sendMessage(player, Messages.KIT_SELECTED.getAsString().replace("{displayname}", displayName));
        for (int i = 0; i < armor.length; i++) {
            if (armor[i] == null) continue;
            ItemStack original = armor[i];
            if (original.getType().toString().startsWith("LEATHER_")) {
                original = new ItemBuilder(original).setColor(team.getTeamColor().getColor()).build();
            }
            player.getInventory().setItem(36 + i, original);
        }
        for (Map.Entry<Integer, ItemStack> entry : contents.entrySet()) {
            if (entry.getValue().getType().toString().contains("WOOL")) {
                ItemStack oldWool = entry.getValue();
                ItemMeta meta = oldWool.getItemMeta();
                ItemStack wool = new ItemBuilder(team.getTeamColor().getWoolMaterial()).setAmount(oldWool.getAmount()).setDisplayName(meta.getDisplayName()).setLore(meta.getLore()).build();
                player.getInventory().setItem(entry.getKey(), wool);
            } else {
                player.getInventory().setItem(entry.getKey(), entry.getValue());
            }
        }
        player.getInventory().setItem(abilitySlot, ability.getItem());
    }

}
