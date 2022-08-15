package me.cubecrafter.woolwars.commands.subcommands;

import me.cubecrafter.woolwars.commands.SubCommand;
import me.cubecrafter.woolwars.menu.menus.ArenasMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ArenasCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        new ArenasMenu((Player) sender).openMenu();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public String getLabel() {
        return "arenas";
    }

    @Override
    public String getPermission() {
        return "woolwars.arenas";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

}
