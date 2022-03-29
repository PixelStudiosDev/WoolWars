package me.cubecrafter.woolwars.commands.subcommands;

import me.cubecrafter.woolwars.commands.SubCommand;
import me.cubecrafter.woolwars.core.Arena;
import me.cubecrafter.woolwars.utils.GameUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LeaveCommand implements SubCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Arena arena = GameUtil.getArenaByPlayer((Player) sender);
        if (arena != null) {
            arena.removePlayer((Player) sender);
        } else {
            sender.sendMessage("You aren't in arena!");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public String getLabel() {
        return "leave";
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

}
