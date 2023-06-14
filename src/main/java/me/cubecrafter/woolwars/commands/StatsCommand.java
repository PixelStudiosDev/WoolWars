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
import me.cubecrafter.woolwars.menu.game.StatsMenu;
import me.cubecrafter.woolwars.storage.player.PlayerManager;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.xutils.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StatsCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        WoolPlayer player = PlayerManager.get((Player) sender);
        // Load stats of specified player
        if (args.length > 0) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (!target.hasPlayedBefore()) {
                player.send(Messages.PLAYER_NOT_FOUND.asString());
                return;
            }
            if (target.isOnline()) {
                WoolPlayer online = PlayerManager.get(target.getPlayer());
                new StatsMenu(player, online.getData()).open();
            } else {
                WoolWars.get().getStorage().fetchData(target.getUniqueId()).thenAccept(data -> {
                    new StatsMenu(player, data).open();
                });
            }
        }
        // Load own stats
        new StatsMenu(player, player.getData()).open();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> names = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            return StringUtil.copyPartialMatches(args[0], names, new ArrayList<>());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public String getLabel() {
        return "stats";
    }

    @Override
    public String getPermission() {
        return "woolwars.stats";
    }

    @Override
    public String getDescription() {
        return "View your own or another player's stats";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

}
