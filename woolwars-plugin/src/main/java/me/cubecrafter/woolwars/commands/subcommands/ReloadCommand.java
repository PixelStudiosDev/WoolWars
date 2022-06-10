package me.cubecrafter.woolwars.commands.subcommands;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.commands.SubCommand;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        WoolWars.getInstance().getFileManager().reload();
        WoolWars.getInstance().getScoreboardHandler().reload();
        WoolWars.getInstance().getKitManager().reload();
        WoolWars.getInstance().getPowerupManager().reload();
        sender.sendMessage(TextUtil.color("&aConfig files reloaded!"));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public String getLabel() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return "woolwars";
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

}
