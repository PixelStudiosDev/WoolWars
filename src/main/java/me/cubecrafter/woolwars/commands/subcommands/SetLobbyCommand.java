package me.cubecrafter.woolwars.commands.subcommands;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.commands.SubCommand;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetLobbyCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Location location = player.getLocation();
        WoolWars.getInstance().getFileManager().getConfig().set("lobby-location", TextUtil.serializeLocation(location));
        WoolWars.getInstance().getFileManager().save();
        TextUtil.sendMessage(player, "{prefix}&7Lobby location set! &8(" + location.getWorld().getName() + ", x: " + location.getBlockX() + ", y: " + location.getBlockY() + ", z: " + location.getBlockZ() + ")");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public String getLabel() {
        return "setlobby";
    }

    @Override
    public String getPermission() {
        return "woolwars.admin";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

}
