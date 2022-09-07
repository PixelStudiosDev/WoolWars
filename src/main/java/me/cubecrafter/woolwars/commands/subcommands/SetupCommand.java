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

import me.cubecrafter.woolwars.arena.setup.SetupSession;
import me.cubecrafter.woolwars.commands.SubCommand;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetupCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (ArenaUtil.getArenaByPlayer(player) != null) {
            TextUtil.sendMessage(player, "{prefix}&cYou can't setup an arena while you're in game!");
            return;
        }
        if (args.length < 2) {
            if (SetupSession.isActive(player)) {
                SetupSession.getSession(player).getMenu().openMenu();
            } else {
                TextUtil.sendMessage(player, "{prefix}&cUsage: /woolwars setup <arena-id>");
            }
            return;
        }
        if (SetupSession.isActive(player)) {
            TextUtil.sendMessage(player, "{prefix}&cYou are already in setup mode!");
            return;
        }
        String id = args[1];
        if (ArenaUtil.getArenaById(id) != null) {
            TextUtil.sendMessage(player, "{prefix}&cAn arena called &e" + id + "&c already exists!");
            return;
        }
        new SetupSession(player, id);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public String getLabel() {
        return "setup";
    }

    @Override
    public String getPermission() {
        return "woolwars.admin";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

}
