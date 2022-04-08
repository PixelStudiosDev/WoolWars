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
        return GameUtil.getArenas().stream().filter(arena -> arena.getPlayers().contains(player) || arena.getSpectators().contains(player)).findAny().orElse(null);
    }

    public Arena getArenaByName(String name) {
        return WoolWars.getInstance().getGameManager().getArena(name);
    }

    public boolean isSpectating(Player player) {
        return GameUtil.getArenas().stream().anyMatch(arena -> arena.getSpectators().contains(player));
    }

    public boolean isPlaying(Player player) {
        return GameUtil.getArenas().stream().anyMatch(arena -> arena.getPlayers().contains(player));
    }

    public List<Arena> getArenas() {
        return new ArrayList<>(WoolWars.getInstance().getGameManager().getArenas().values());
    }

}
