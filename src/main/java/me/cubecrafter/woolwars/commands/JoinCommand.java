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

import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.arena.ArenaUtil;
import me.cubecrafter.woolwars.storage.player.PlayerManager;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.xutils.TextUtil;
import me.cubecrafter.xutils.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JoinCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) return;

        Arena arena = ArenaUtil.getArenaById(args[0]);
        WoolPlayer player = PlayerManager.get((Player) sender);

        if (arena != null) {
            arena.addPlayer(player, true);
        } else if (ArenaUtil.getGroups().contains(args[0])) {
            ArenaUtil.joinRandomArena(player, args[0]);
        } else if (args[0].equalsIgnoreCase("random")) {
            ArenaUtil.joinRandomArena(player);
        } else {
            TextUtil.sendMessage(sender, Messages.ARENA_NOT_FOUND.asString());
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length != 1) return null;

        List<String> completions = new ArrayList<>();
        completions.addAll(ArenaUtil.getArenas().stream().filter(arena -> arena.getState().equals(GameState.WAITING) || arena.getState().equals(GameState.STARTING)).map(Arena::getId).collect(Collectors.toList()));
        completions.addAll(ArenaUtil.getGroups());
        completions.add("random");

        return completions;
    }

    @Override
    public String getLabel() {
        return "join";
    }

    @Override
    public String getPermission() {
        return "woolwars.join";
    }

    @Override
    public String getDescription() {
        return "Join an arena/group";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

}
