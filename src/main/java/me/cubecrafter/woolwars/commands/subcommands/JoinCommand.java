package me.cubecrafter.woolwars.commands.subcommands;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import me.cubecrafter.woolwars.game.arena.Arena;
import me.cubecrafter.woolwars.utils.GameUtil;
import org.bukkit.entity.Player;

public class JoinCommand {

    @Command(name = "join", desc = "Join a game")
    public void joinCommand(@Sender Player player, String arena) {
        Arena found = GameUtil.getArenaById(arena);
        if (found != null) {
            found.addPlayer(player);
        }
    }
}
