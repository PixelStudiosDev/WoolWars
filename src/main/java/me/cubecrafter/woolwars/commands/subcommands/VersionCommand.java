package me.cubecrafter.woolwars.commands.subcommands;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.commands.SubCommand;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.command.CommandSender;

import java.util.List;

public class VersionCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(TextUtil.color("&8&m---------------------------------------------"));
        sender.sendMessage(TextUtil.color("&cWoolWars &fv" + WoolWars.getInstance().getDescription().getVersion() + " &7by &bCubeCrafter"));
        sender.sendMessage(TextUtil.color("&7Running on: &f" + WoolWars.getInstance().getServer().getVersion()));
        sender.sendMessage(TextUtil.color("&7Java Version: &f" + System.getProperty("java.version")));
        sender.sendMessage(TextUtil.color("&7PlaceholderAPI Hook: " + (WoolWars.getInstance().isPAPIEnabled() ? "&aEnabled" : "&cDisabled")));
        sender.sendMessage(TextUtil.color("&7Plugin registered to &d%%__USERNAME__%%"));
        sender.sendMessage(TextUtil.color("&8&m---------------------------------------------"));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public String getLabel() {
        return "version";
    }

    @Override
    public String getPermission() {
        return "woolwars.admin";
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    private final String user = "%%__USER__%%";

}
