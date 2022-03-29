package me.cubecrafter.woolwars.commands;

import me.cubecrafter.woolwars.commands.subcommands.JoinCommand;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements TabExecutor {

    private final List<SubCommand> subCommands = new ArrayList<>();

    public CommandManager() {
        subCommands.add(new JoinCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            for (SubCommand subCommand : subCommands) {
                if (subCommand.getLabel().equalsIgnoreCase(args[0])) {
                    if (subCommand.isPlayerOnly() && !(sender instanceof Player)) {
                        TextUtil.severe("This command can be executed only by a player!");
                        return true;
                    }
                    if (subCommand.getPermission() != null) {
                        if (!sender.hasPermission(subCommand.getPermission())) {
                            sender.sendMessage(TextUtil.color("&cYou don't have the permission to execute this command!"));
                            return true;
                        }
                    }
                    subCommand.onCommand(sender, args);
                    return true;
                }
            }
        } else {
            if (sender instanceof Player) {

            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length > 1) {
            for (SubCommand subCommand : subCommands) {
                if (subCommand.getLabel().equalsIgnoreCase(args[0])) {
                    if (subCommand.getPermission() != null) {
                        if (!sender.hasPermission(subCommand.getPermission())) {
                            return null;
                        }
                    }
                    return subCommand.onTabComplete(sender, args);
                }
            }
        }
        List<String> subCmd = new ArrayList<>();
        if (args.length == 1) {
            for (SubCommand subCommand : subCommands) {
                if (subCommand.getPermission() != null) {
                    if (sender.hasPermission(subCommand.getPermission())) {
                        subCmd.add(subCommand.getLabel());
                    }
                } else {
                    subCmd.add(subCommand.getLabel());
                }
            }
            return subCmd;
        }
        return null;
    }

    public List<SubCommand> getSubCommands() {
        return subCommands;
    }

}
