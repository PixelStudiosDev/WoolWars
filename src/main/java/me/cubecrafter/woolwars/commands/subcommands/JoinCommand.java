package me.cubecrafter.woolwars.commands.subcommands;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class JoinCommand implements SubCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        WoolWars.getInstance().getGameManager().getArena().addPlayer((Player) sender);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public String getLabel() {
        return "join";
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
