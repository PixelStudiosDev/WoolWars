package me.cubecrafter.woolwars.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {

    void onCommand(CommandSender sender, String[] args);
    List<String> onTabComplete(CommandSender sender, String[] args);
    String getLabel();
    String getPermission();
    boolean isPlayerOnly();

}
