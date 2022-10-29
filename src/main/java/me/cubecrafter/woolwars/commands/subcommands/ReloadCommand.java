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

package me.cubecrafter.woolwars.commands.subcommands;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.commands.SubCommand;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        WoolWars.getInstance().reload();
        TextUtil.sendMessage(sender, Messages.CONFIG_RELOADED.getAsString());
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
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
    public boolean isPlayerOnly() {
        return false;
    }

}
