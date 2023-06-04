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
import me.cubecrafter.woolwars.api.events.player.PlayerLeaveArenaEvent;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.ArenaUtil;
import me.cubecrafter.woolwars.storage.player.PlayerManager;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.xutils.commands.BaseCommand;
import me.cubecrafter.xutils.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class LeaveCommand extends BaseCommand implements SubCommand {

    public LeaveCommand() {
        super("leave");

        setDescription("Leave the current arena");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        WoolPlayer player = PlayerManager.get((Player) sender);
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        arena.removePlayer(player, PlayerLeaveArenaEvent.Reason.QUIT);
    }

    @Override
    public String getLabel() {
        return "leave";
    }

    @Override
    public String getPermission() {
        return "woolwars.leave";
    }

    @Override
    public String getDescription() {
        return "Leave the current arena";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public Plugin getPlugin() {
        return WoolWars.get();
    }

}
