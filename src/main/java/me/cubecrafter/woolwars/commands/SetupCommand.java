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

import me.cubecrafter.woolwars.arena.ArenaUtil;
import me.cubecrafter.woolwars.arena.setup.SetupSession;
import me.cubecrafter.woolwars.storage.player.PlayerManager;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.xutils.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetupCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        WoolPlayer player = PlayerManager.get((Player) sender);

        if (ArenaUtil.getArenaByPlayer(player) != null) {
            player.send("&cYou can't setup an arena while you're in game!");
            return;
        }

        if (args.length < 1) {
            if (SetupSession.hasSession(player)) {
                SetupSession.get(player).getMenu().open();
            } else {
                player.send("&cUsage: /woolwars setup <arena-id>");
            }
            return;
        }

        if (SetupSession.hasSession(player)) {
            player.send("&cYou are already in setup mode!");
            return;
        }

        String id = args[0];
        if (ArenaUtil.getArenaById(id) != null) {
            player.send("&cAn arena called &e" + id + "&c already exists!");
            return;
        }

        new SetupSession(player, id);
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
    public String getDescription() {
        return "Setup an arena";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

}
