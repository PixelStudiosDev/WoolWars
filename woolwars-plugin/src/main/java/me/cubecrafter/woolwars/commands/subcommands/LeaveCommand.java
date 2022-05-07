package me.cubecrafter.woolwars.commands.subcommands;

import me.cubecrafter.woolwars.commands.SubCommand;
import me.cubecrafter.woolwars.game.arena.Arena;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LeaveCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        arena.removePlayer(player, true);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public String getLabel() {
        return "leave";
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
