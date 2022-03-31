package me.cubecrafter.woolwars.utils;

import lombok.experimental.UtilityClass;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.core.Arena;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class GameUtil {

    public Arena getArenaByPlayer(Player player) {
        for (Arena arena : GameUtil.getArenas()) {
            if (arena.getPlayers().contains(player) || arena.getSpectators().contains(player)) {
                return arena;
            }
        }
        return null;
    }

    public Arena getArenaByName(String name) {
        return WoolWars.getInstance().getGameManager().getArenas().get(name);
    }

    public boolean isSpectating(Player player) {
        for (Arena arena : GameUtil.getArenas()) {
            if (arena.getSpectators().contains(player)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPlaying(Player player) {
        for (Arena arena : GameUtil.getArenas()) {
            if (arena.getPlayers().contains(player)) {
                return true;
            }
        }
        return false;
    }

    public List<Arena> getArenas() {
        return new ArrayList<>(WoolWars.getInstance().getGameManager().getArenas().values());
    }

}
