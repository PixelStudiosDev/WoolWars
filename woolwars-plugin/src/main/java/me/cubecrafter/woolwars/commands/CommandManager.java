package me.cubecrafter.woolwars.commands;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.commands.subcommands.*;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.menu.menus.MainMenu;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class CommandManager implements TabExecutor {

    private final Set<SubCommand> subCommands = new HashSet<>();

    public CommandManager(WoolWars plugin) {
        subCommands.addAll(Arrays.asList(new HelpCommand(), new JoinCommand(), new LeaveCommand(), new MenuCommand(), new ForceStartCommand(), new ReloadCommand(), new StatsCommand()));
        PluginCommand command = plugin.getCommand("woolwars");
        command.setExecutor(this);
        command.setTabCompleter(this);
        if (Configuration.ENABLE_LEAVE_COMMAND_SHORTCUT.getAsBoolean()) {
            plugin.getCommand("leave").setExecutor(new LeaveCommand());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            SubCommand cmd = subCommands.stream().filter(sub -> sub.getLabel().equalsIgnoreCase(args[0])).findAny().orElse(null);
            if (cmd == null) {
                sender.sendMessage(TextUtil.color("&cUnknown subcommand!"));
                return true;
            }
            if (cmd.isPlayerOnly() && !(sender instanceof Player)) {
                sender.sendMessage(TextUtil.color("&cThis command can be executed only by a player!"));
                return true;
            }
            if (!sender.hasPermission(cmd.getPermission())) {
                sender.sendMessage(TextUtil.color("&cYou don't have the permission to execute this command!"));
                return true;
            }
            cmd.execute(sender, args);
        } else if (sender instanceof Player) {
            new MainMenu((Player) sender).openMenu();
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length > 1) {
            SubCommand cmd = subCommands.stream().filter(sub -> sub.getLabel().equalsIgnoreCase(args[0])).findAny().orElse(null);
            if (cmd == null) {
                return Collections.emptyList();
            }
            if (sender.hasPermission(cmd.getPermission())) {
                return cmd.tabComplete(sender, args) != null ? cmd.tabComplete(sender, args) : Collections.emptyList();
            }
        } else if (args.length == 1) {
            if (args[0].isEmpty()) {
                return subCommands.stream().filter(cmd -> sender.hasPermission(cmd.getPermission())).map(SubCommand::getLabel).collect(Collectors.toList());
            } else {
                return subCommands.stream().filter(sub -> sender.hasPermission(sub.getPermission())).map(SubCommand::getLabel).filter(label -> label.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

}
