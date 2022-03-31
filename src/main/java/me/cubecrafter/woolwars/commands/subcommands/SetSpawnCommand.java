package me.cubecrafter.woolwars.commands.subcommands;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Require;
import com.jonahseguin.drink.annotation.Sender;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Infinity
 * 31-03-2022 / 10:11 PM
 * WoolWars1 / me.cubecrafter.woolwars.commands.subcommands
 */

public class SetSpawnCommand {

    @Command(name = "setspawn", aliases = {"setspawnlocation"}, desc = "SetSpawn of the lobby", usage = "/woolwars setspawn")
    @Require(value = "woolwars.command.setspawn")
    public void setSpawnCommand(@Sender Player player) {
        if (!TextUtil.isSpawnRegistered(WoolWars.getInstance().getFileManager().getConfig().getString("spawn-location"))) {
            Location location = player.getLocation();
            String serializedLoc = TextUtil.serializeLocation(location);
            WoolWars.getInstance().getFileManager().getConfig().set("spawn-location", serializedLoc);
            player.sendMessage(TextUtil.color("&aSpawn has been successfully registered."));
        } else {
            player.sendMessage(TextUtil.color("&cSpawn already exists! clear spawn-location data from config to reset spawn"));
        }
    }
}