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

import me.cubecrafter.woolwars.menu.game.ArenasMenu;
import me.cubecrafter.woolwars.storage.player.PlayerManager;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.xutils.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenasCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        WoolPlayer player = PlayerManager.get((Player) sender);
        new ArenasMenu(player).open();
    }

    @Override
    public String getLabel() {
        return "arenas";
    }

    @Override
    public String getPermission() {
        return "woolwars.arenas";
    }

    @Override
    public String getDescription() {
        return "Open the arenas menu";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

}
