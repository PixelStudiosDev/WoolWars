package me.cubecrafter.woolwars.commands.subcommands;

import me.cubecrafter.woolwars.commands.SubCommand;
import me.cubecrafter.woolwars.menu.menus.StatsMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class StatsCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        new StatsMenu((Player) sender).openMenu();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
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
    public boolean isPlayerOnly() {
        return true;
    }

}
