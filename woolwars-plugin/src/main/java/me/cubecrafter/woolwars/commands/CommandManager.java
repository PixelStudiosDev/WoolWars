package me.cubecrafter.woolwars.commands;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.commands.subcommands.*;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class CommandManager implements TabExecutor {

    private final Set<SubCommand> subCommands = new HashSet<>();

    public CommandManager(WoolWars plugin) {
        subCommands.add(new JoinCommand());
        subCommands.add(new LeaveCommand());
        subCommands.add(new ArenasCommand());
        subCommands.add(new ForceStartCommand());
        subCommands.add(new ReloadCommand());
        subCommands.add(new StatsCommand());
        subCommands.add(new VersionCommand());
        subCommands.add(new SetLobbyCommand());
        subCommands.add(new SetupCommand());
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
                sender.sendMessage(TextUtil.color(Messages.UNKNOWN_COMMAND.getAsString().replace("{prefix}", Messages.PREFIX.getAsString())));
                return true;
            }
            if (cmd.isPlayerOnly() && !(sender instanceof Player)) {
                sender.sendMessage(TextUtil.color(Messages.ONLY_PLAYER_COMMAND.getAsString().replace("{prefix}", Messages.PREFIX.getAsString())));
                return true;
            }
            if (!sender.hasPermission(cmd.getPermission())) {
                sender.sendMessage(TextUtil.color(Messages.NO_PERMISSION.getAsString().replace("{prefix}", Messages.PREFIX.getAsString())));
                return true;
            }
            cmd.execute(sender, args);
        } else {
            sender.sendMessage(TextUtil.color(Messages.UNKNOWN_COMMAND.getAsString().replace("{prefix}", Messages.PREFIX.getAsString())));
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
