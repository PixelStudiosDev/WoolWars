/*
 * Wool Wars
 * Copyright (C) 2023 CubeCrafter Development
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

import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.xutils.TextUtil;
import me.cubecrafter.xutils.commands.BaseCommand;
import me.cubecrafter.xutils.commands.CommandManager;
import me.cubecrafter.xutils.commands.SubCommand;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class WoolCommand extends BaseCommand {

    public WoolCommand() {
        super("woolwars");

        setAliases(Collections.singletonList("ww"));
        setDescription("Main command for Wool Wars");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            sendHelpMenu((Player) sender);
        } else {
            TextUtil.sendMessage(sender, getUnknownCommandMessage());
        }
    }

    @Override
    public String getOnlyPlayerMessage() {
        return Messages.ONLY_PLAYER_COMMAND.asString();
    }

    @Override
    public String getUnknownCommandMessage() {
        return Messages.UNKNOWN_COMMAND.asString();
    }

    @Override
    public String getPermissionMessage() {
        return Messages.NO_PERMISSION.asString();
    }

    public void sendHelpMenu(Player player) {
        HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8Click to suggest this command!").create());

        ComponentBuilder builder = new ComponentBuilder("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬\n");
        for (SubCommand command : getSubCommands().values()) {
            if (command.getPermission() == null || player.hasPermission(command.getPermission())) {
                ClickEvent click = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/woolwars " + command.getLabel());
                builder.append("§c/woolwars " + command.getLabel() + " §8- §7" + command.getDescription() + "\n").event(hover).event(click);
            }
        }
        builder.append("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬").event((HoverEvent) null).event((ClickEvent) null);

        player.spigot().sendMessage(builder.create());
    }

    public static void register() {
        CommandManager manager = CommandManager.get();

        BaseCommand command = manager.register(new WoolCommand());
        command.registerSub(
                new ArenasCommand(),
                new ForceStartCommand(),
                new HelpCommand(),
                new JoinCommand(),
                new LeaveCommand(),
                new ReloadCommand(),
                new SetLobbyCommand(),
                new SetupCommand(),
                new StatsCommand()
        );

        if (Config.ENABLE_LEAVE_COMMAND_SHORTCUT.asBoolean()) {
            manager.register(new LeaveCommand());
        }
    }

}
