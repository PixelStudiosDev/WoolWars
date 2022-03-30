package me.cubecrafter.woolwars.utils;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.core.Arena;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameUtil {

    public static Arena getArenaByPlayer(Player player) {
        for (Arena arena : GameUtil.getArenas()) {
            if (arena.getPlayers().contains(player)) {
                return arena;
            }
        }
        return null;
    }

    public static boolean isSpectator(Player player) {
        for (Arena arena : GameUtil.getArenas()) {
            if (arena.getSpectators().contains(player)) {
                return true;
            }
        }
        return false;
    }

    public static List<Arena> getArenas() {
        return new ArrayList<>(WoolWars.getInstance().getGameManager().getArenas().values());
    }

}
