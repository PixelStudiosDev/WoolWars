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

package me.cubecrafter.woolwars.commands.subcommands;

import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.commands.SubCommand;
import me.cubecrafter.woolwars.arena.ArenaUtil;
import me.cubecrafter.woolwars.storage.player.PlayerManager;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ForceStartCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        WoolPlayer player = PlayerManager.get((Player) sender);
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        arena.forceStart();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public String getLabel() {
        return "forcestart";
    }

    @Override
    public String getPermission() {
        return "woolwars.forcestart";
    }

    @Override
    public String getDescription() {
        return "Force start the game";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

}
