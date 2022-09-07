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
