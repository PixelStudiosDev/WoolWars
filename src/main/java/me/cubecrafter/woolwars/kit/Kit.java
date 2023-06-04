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

package me.cubecrafter.woolwars.kit;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.team.Team;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.kit.ability.Ability;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Kit {

    private final static KitManager manager = WoolWars.get().getKitManager();

    private final YamlConfiguration config;
    private final String id;
    private final String name;
    private final boolean permissionRequired;

    private final Map<Integer, ItemStack> contents = new HashMap<>();
    private final ItemStack[] armor = new ItemStack[4];

    private final Ability ability;

    public Kit(String id, YamlConfiguration config) {
        this.id = id;
        this.config = config;
        this.name = config.getString("name");
        this.permissionRequired = config.getBoolean("permission-required");
        this.ability = Ability.fromConfig(config.getConfigurationSection("ability"));

        for (String section : config.getConfigurationSection("items").getKeys(false)) {
            ItemStack item = ItemBuilder.fromConfig(config.getConfigurationSection("items." + section)).setUnbreakable(true).build();
            contents.put(config.getInt("items." + section + ".slot"), item);
        }

        String[] parts = {"boots", "leggings", "chestplate", "helmet"};
        for (int i = 0; i < 4; i++) {
            ConfigurationSection section = config.getConfigurationSection("armor." + parts[i]);
            if (section == null) continue;
            armor[i] = ItemBuilder.fromConfig(section).setUnbreakable(true).build();
        }
    }

    public void addToPlayer(WoolPlayer player, Team team) {
        if (!canUse(player)) {
            player.send(Messages.KIT_NO_PERMISSION.asString());
            return;
        }
        player.getData().setSelectedKit(id);

        PlayerInventory inventory = player.getPlayer().getInventory();
        inventory.clear();
        inventory.setArmorContents(null);

        ItemStack[] armor = this.armor.clone();
        // Color leather armor
        for (int i = 0; i < 4; i++) {
            ItemStack item = armor[i];
            if (item == null) continue;
            if (item.getType().toString().startsWith("LEATHER_")) {
                armor[i] = new ItemBuilder(item).setColor(team.getTeamColor().getColor()).build();
            }
        }
        inventory.setArmorContents(armor);

        for (Map.Entry<Integer, ItemStack> entry : contents.entrySet()) {
            ItemStack item = entry.getValue();
            if (item.getType().toString().contains("WOOL")) {
                ItemStack wool = new ItemBuilder(team.getTeamColor().getWoolMaterial()).setAmount(item.getAmount()).build();
                // Copy old meta
                wool.setItemMeta(item.getItemMeta());
                inventory.setItem(entry.getKey(), wool);
            } else {
                inventory.setItem(entry.getKey(), item);
            }
        }
        inventory.setItem(ability.getSlot(), ability.getItem());

        player.send(Messages.KIT_SELECTED.asString().replace("{name}", name));
    }

    public boolean canUse(WoolPlayer player) {
        return !permissionRequired || player.hasPermission("woolwars.kit." + id);
    }

}
