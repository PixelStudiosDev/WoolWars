package me.cubecrafter.woolwars.commands.subcommands;

import me.cubecrafter.woolwars.commands.SubCommand;
import me.cubecrafter.woolwars.menu.menus.GameMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MenuCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        new GameMenu((Player) sender).openMenu();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public String getLabel() {
        return "menu";
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
