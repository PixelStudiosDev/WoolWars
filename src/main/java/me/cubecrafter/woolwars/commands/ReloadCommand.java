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

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.xutils.TextUtil;
import me.cubecrafter.xutils.commands.SubCommand;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        WoolWars.get().reload();
        TextUtil.sendMessage(sender, Messages.CONFIG_RELOADED.asString());
    }

    @Override
    public String getLabel() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return "woolwars.admin";
    }

    @Override
    public String getDescription() {
        return "Reloads the configuration";
    }

}
