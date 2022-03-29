package me.cubecrafter.woolwars.commands.subcommands;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import me.cubecrafter.woolwars.WoolWars;
import org.bukkit.entity.Player;

public class JoinCommand {

    @Command(name = "join", desc = "Join a game")
    public void joinMatch(@Sender Player player){
        WoolWars.getInstance().getGameManager().getArena().addPlayer(player);
    }
}
