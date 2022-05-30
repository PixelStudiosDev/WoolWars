package me.cubecrafter.woolwars.commands.subcommands;

import me.cubecrafter.woolwars.commands.SubCommand;
import me.cubecrafter.woolwars.game.arena.Arena;
import me.cubecrafter.woolwars.game.arena.ArenaState;
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
            ArenaUtil.joinRandomFromGroup((Player) sender, args[1]);
        } else {
            TextUtil.sendMessage((Player) sender, "&cArena or group not found!");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length != 2) return null;
        List<String> completions = new ArrayList<>();
        completions.addAll(ArenaUtil.getArenas().stream().filter(arena -> arena.getArenaState().equals(ArenaState.WAITING) || arena.getArenaState().equals(ArenaState.STARTING)).map(Arena::getId).collect(Collectors.toList()));
        completions.addAll(ArenaUtil.getGroups());
        return completions;
    }

    @Override
    public String getLabel() {
        return "join";
    }

    @Override
    public String getPermission() {
        return "woolwars";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

}
