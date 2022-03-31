package me.cubecrafter.woolwars.commands.subcommands;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Infinity
 * 31-03-2022 / 10:25 PM
 * WoolWars1 / me.cubecrafter.woolwars.commands.subcommands
 */

public class SpawnCommand {

    @Command(name = "", desc = "Teleport to spawn")
    public void spawnCommand(@Sender Player player){
        if (!TextUtil.isSpawnRegistered(WoolWars.getInstance().getFileManager().getConfig().getString("spawn-location"))){
            player.sendMessage(TextUtil.color("&cSpawn is yet to be registered, please contact an admin!"));
        } else {
            Location location = TextUtil.deserializeLocation(WoolWars.getInstance().getFileManager().getConfig().getString("spawn-location"));
            player.teleport(location);
        }
    }

}
