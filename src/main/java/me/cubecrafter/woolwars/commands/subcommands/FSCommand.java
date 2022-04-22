package me.cubecrafter.woolwars.commands.subcommands;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.utils.GameUtil;
import org.bukkit.entity.Player;

public class FSCommand {

    @Command(name = "forcestart", desc = "forcestart")
    public void forcestart(@Sender Player player) {
        Arena arena = GameUtil.getArenaByPlayer(player);
        if (arena != null) {
            arena.forceStart();
        }
    }

}
