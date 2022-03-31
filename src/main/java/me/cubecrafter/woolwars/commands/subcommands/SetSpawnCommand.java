package me.cubecrafter.woolwars.commands.subcommands;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Require;
import com.jonahseguin.drink.annotation.Sender;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetSpawnCommand {

    @Command(name = "setlobby", desc = "Set the lobby location")
    @Require(value = "woolwars.admin")
    public void setSpawnCommand(@Sender Player player) {
        Location location = player.getLocation();
        String serializedLoc = TextUtil.serializeLocation(location);
        WoolWars.getInstance().getFileManager().getConfig().set("lobby-location", serializedLoc);
        WoolWars.getInstance().getFileManager().save();
        player.sendMessage(TextUtil.color("&aLobby location set!"));
    }

}