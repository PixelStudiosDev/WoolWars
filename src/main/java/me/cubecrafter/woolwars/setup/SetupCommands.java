package me.cubecrafter.woolwars.setup;

import com.jonahseguin.drink.annotation.OptArg;
import com.jonahseguin.drink.annotation.Require;
import com.jonahseguin.drink.annotation.Sender;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

/**
 * @author Infinity
 * 14-04-2022 / 04:58 PM
 * WoolWars / me.cubecrafter.woolwars.setup
 */

public class SetupCommands {

    /*
    @Command(name = "", desc = "Arena Command Base")
    @Require("woolwars.admin")
    public void arenaCommand(@Sender Player player){

    }

    @Command(name = "create", desc = "Arena Creation Command", usage = "<arenaName> <arenaWorld> <maxTeamPlayers> <minPlayers>")
    @Require("woolwars.admin")
    public void createArena(@Sender Player sender, String arenaName, String arenaWorldName, int maxTeamPlayers, int minPlayers) throws IOException {
        SetupHandler.createArena(arenaName, maxTeamPlayers, minPlayers);
        World world = Bukkit.getWorld(arenaWorldName);
        sender.teleport(new Location(world, world.getSpawnLocation().getX(), world.getSpawnLocation().getY(), world.getSpawnLocation().getZ()));
        sender.sendMessage(TextUtil.color("&eNow use &c/arena " + arenaName + " setlobbylocation &eto set the lobby location"));
    }
    */


}
