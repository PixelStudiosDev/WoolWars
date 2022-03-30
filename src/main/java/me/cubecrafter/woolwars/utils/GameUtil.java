package me.cubecrafter.woolwars.utils;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.core.Arena;
import org.bukkit.entity.Player;

public class GameUtil {

    public static Arena getArenaByPlayer(Player player) {
        for (Arena arena : WoolWars.getInstance().getGameManager().getArenas().values()) {
            if (arena.getPlayers().contains(player)) {
                return arena;
            }
        }
        return null;
    }

}
