package me.cubecrafter.woolwars.commands.subcommands;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import me.cubecrafter.woolwars.core.Arena;
import me.cubecrafter.woolwars.utils.GameUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.entity.Player;

public class LeaveCommand {

    @Command(name = "leave", desc = "Leave the current game")
    public void leaveCommand(@Sender Player player) {
        Arena arena = GameUtil.getArenaByPlayer(player);
        if (arena != null) {
            arena.removePlayer(player);
        } else {
            player.sendMessage(TextUtil.color("You aren't in arena!"));
        }
    }

}
