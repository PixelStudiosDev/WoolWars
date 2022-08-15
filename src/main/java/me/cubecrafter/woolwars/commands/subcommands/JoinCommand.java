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
