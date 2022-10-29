/*
 * Wool Wars
 * Copyright (C) 2022 CubeCrafter Development
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.cubecrafter.woolwars.commands;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.commands.subcommands.ArenasCommand;
import me.cubecrafter.woolwars.commands.subcommands.ForceStartCommand;
import me.cubecrafter.woolwars.commands.subcommands.JoinCommand;
import me.cubecrafter.woolwars.commands.subcommands.LeaveCommand;
import me.cubecrafter.woolwars.commands.subcommands.ReloadCommand;
import me.cubecrafter.woolwars.commands.subcommands.SetLobbyCommand;
import me.cubecrafter.woolwars.commands.subcommands.SetupCommand;
import me.cubecrafter.woolwars.commands.subcommands.StatsCommand;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CommandManager implements TabExecutor {

    private final SubCommand[] commands = {
            new ArenasCommand(),
            new ForceStartCommand(),
            new JoinCommand(),
            new LeaveCommand(),
            new ReloadCommand(),
            new SetLobbyCommand(),
            new SetupCommand(),
            new StatsCommand()
    };

    public CommandManager(WoolWars plugin) {
        PluginCommand command = plugin.getCommand("woolwars");
        command.setExecutor(this);
        command.setTabCompleter(this);
        if (Config.ENABLE_LEAVE_COMMAND_SHORTCUT.getAsBoolean()) {
            plugin.getCommand("leave").setExecutor(new LeaveCommand());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            SubCommand cmd = Arrays.stream(commands).filter(sub -> sub.getLabel().equalsIgnoreCase(args[0])).findAny().orElse(null);
            if (cmd == null) {
                TextUtil.sendMessage(sender, Messages.UNKNOWN_COMMAND.getAsString());
                return true;
            }
            if (cmd.isPlayerOnly() && !(sender instanceof Player)) {
                TextUtil.sendMessage(sender, Messages.ONLY_PLAYER_COMMAND.getAsString());
                return true;
            }
            if (!sender.hasPermission(cmd.getPermission())) {
                TextUtil.sendMessage(sender, Messages.NO_PERMISSION.getAsString());
                return true;
            }
            cmd.execute(sender, Arrays.copyOfRange(args, 1, args.length));
        } else {
            TextUtil.sendMessage(sender, Messages.UNKNOWN_COMMAND.getAsString());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length > 1) {
            SubCommand cmd = Arrays.stream(commands).filter(sub -> sub.getLabel().equalsIgnoreCase(args[0])).findAny().orElse(null);
            if (cmd == null) {
                return Collections.emptyList();
            }
            if (sender.hasPermission(cmd.getPermission())) {
                List<String> completions = cmd.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
                return completions == null ? Collections.emptyList() : completions;
            }
        } else if (args.length == 1) {
            List<String> completions = Arrays.stream(commands).filter(sub -> sender.hasPermission(sub.getPermission())).map(SubCommand::getLabel).collect(Collectors.toList());
            if (args[0].isEmpty()) {
                return completions;
            } else {
                return completions.stream().filter(label -> label.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

}
