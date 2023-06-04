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

import me.cubecrafter.xutils.commands.CommandManager;
import me.cubecrafter.xutils.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        WoolCommand command = (WoolCommand) CommandManager.get().getCommand("woolwars");
        command.sendHelpMenu((Player) sender);
    }

    @Override
    public String getLabel() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Show the help menu";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

}
