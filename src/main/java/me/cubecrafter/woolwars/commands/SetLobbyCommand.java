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

package me.cubecrafter.woolwars.commands;

import me.cubecrafter.xutils.TextUtil;
import me.cubecrafter.xutils.commands.SubCommand;
import me.cubecrafter.xutils.config.ConfigManager;
import me.cubecrafter.xutils.config.Configuration;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetLobbyCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Location location = player.getLocation();

        Configuration config = ConfigManager.get().load("config.yml");
        config.set("lobby-location", TextUtil.fromLocation(location));
        config.save();

        TextUtil.sendMessage(player, "&7Lobby location set! &8(" + location.getWorld().getName() + ", x: " + location.getBlockX() + ", y: " + location.getBlockY() + ", z: " + location.getBlockZ() + ")");
    }

    @Override
    public String getLabel() {
        return "setlobby";
    }

    @Override
    public String getPermission() {
        return "woolwars.admin";
    }

    @Override
    public String getDescription() {
        return "Sets the lobby location";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

}
