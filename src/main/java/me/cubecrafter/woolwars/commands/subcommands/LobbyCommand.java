package me.cubecrafter.woolwars.commands.subcommands;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LobbyCommand {

    @Command(name = "", desc = "Teleport to lobby")
    public void spawnCommand(@Sender Player player){
        if (!WoolWars.getInstance().getFileManager().getConfig().isSet("lobby-location")) {
            player.sendMessage(TextUtil.color("&cLobby location not set! Please contact an admin!"));
        } else {
            Location location = TextUtil.deserializeLocation(WoolWars.getInstance().getFileManager().getConfig().getString("lobby-location"));
            player.teleport(location);
        }
    }

}
