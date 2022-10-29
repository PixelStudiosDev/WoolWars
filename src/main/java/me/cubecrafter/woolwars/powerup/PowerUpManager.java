/*
 * Wool Wars
 * Copyright (C) 2022 CubeCrafter Development
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

package me.cubecrafter.woolwars.powerup;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PowerUpManager {

    private final List<PowerUpType> powerUps = new ArrayList<>();

    public void load() {
        powerUps.clear();
        YamlConfiguration config = WoolWars.getInstance().getFileManager().getPowerUps();
        int loaded = 0;
        for (String id : config.getKeys(false)) {
            ItemStack displayedItem = ItemBuilder.fromConfig(config.getConfigurationSection(id + ".displayed-item")).build();
            List<String> holoLines = config.getStringList(id + ".hologram-lines");
            List<ItemStack> items = new ArrayList<>();
            if (config.contains(id + ".items")) {
                for (String item : config.getConfigurationSection(id + ".items").getKeys(false)) {
                    ItemStack created = ItemBuilder.fromConfig(config.getConfigurationSection(id + ".items." + item)).build();
                    items.add(created);
                }
            }
            List<PotionEffect> effects = new ArrayList<>();
            if (config.contains(id + ".effects")) {
                for (String effect : config.getStringList(id + ".effects")) {
                    PotionEffect created = TextUtil.getEffect(effect);
                    effects.add(created);
                }
            }
            PowerUpType data = new PowerUpType(displayedItem, holoLines, items, effects);
            powerUps.add(data);
            loaded++;
        }
        TextUtil.info("Loaded " + loaded + " powerups!");
    }

    public PowerUpType getRandom() {
        return powerUps.get(ThreadLocalRandom.current().nextInt(powerUps.size()));
    }

}
