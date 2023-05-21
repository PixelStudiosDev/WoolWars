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

package me.cubecrafter.woolwars.kit.ability;

import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.ArenaUtil;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.woolwars.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class CustomAbility extends Ability {

    public CustomAbility(ConfigurationSection section) {
        super(section, Type.CUSTOM);
    }

    @Override
    public boolean handle(WoolPlayer player, Arena arena) {
        List<String> actions = section.getStringList("actions");
        // Action format: <TYPE>:<ARGS>
        for (String action : actions) {
            if (!action.contains(":")) continue;

            String type = action.substring(0, action.indexOf(":")).toUpperCase();
            String args = action.substring(action.indexOf(":") + 1).replace("{player}", player.getName());
            args = ArenaUtil.parsePlaceholders(player, args, arena);

            switch (type) {
                case "CONSOLE":
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), args);
                    break;
                case "COMMAND":
                    Bukkit.dispatchCommand(player.getPlayer(), args);
                    break;
                case "MESSAGE":
                    player.send(args);
                    break;
                case "EFFECT":
                    player.getPlayer().addPotionEffect(Utils.parseEffect(args));
                    break;
                case "SOUND":
                    player.playSound(args);
                    break;
            }
        }
        return true;
    }

}
