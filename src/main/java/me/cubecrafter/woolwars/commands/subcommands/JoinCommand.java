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

import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.commands.SubCommand;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JoinCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) return;
        Arena arena = ArenaUtil.getArenaById(args[1]);
        if (arena != null) {
            arena.addPlayer((Player) sender);
        } else if (ArenaUtil.getGroups().contains(args[1])) {
            ArenaUtil.joinRandomArena((Player) sender, args[1]);
        } else if (args[1].equalsIgnoreCase("random")) {
            ArenaUtil.joinRandomArena((Player) sender);
        } else {
            TextUtil.sendMessage((Player) sender, Messages.ARENA_NOT_FOUND.getAsString());
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length != 2) return null;
        List<String> completions = new ArrayList<>();
        completions.addAll(ArenaUtil.getArenas().stream().filter(arena -> arena.getGameState().equals(GameState.WAITING) || arena.getGameState().equals(GameState.STARTING)).map(Arena::getId).collect(Collectors.toList()));
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
    public boolean isPlayerOnly() {
        return true;
    }

}
