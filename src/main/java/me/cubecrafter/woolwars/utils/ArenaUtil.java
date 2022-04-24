package me.cubecrafter.woolwars.utils;

import lombok.experimental.UtilityClass;
import me.cubecrafter.woolwars.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@UtilityClass
public class ArenaUtil {

    public void hidePlayersOutsideArena(Player player, Arena arena) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (arena.getPlayers().contains(online)) continue;
            online.hidePlayer(player);
            player.hidePlayer(online);
        }
        for (Player arenaPlayer : arena.getPlayers()) {
            arenaPlayer.showPlayer(player);
            player.showPlayer(arenaPlayer);
        }
    }

    public void showPlayersOutsideArena(Player player) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (GameUtil.getArenaByPlayer(online) != null) continue;
            online.showPlayer(player);
            player.showPlayer(online);
        }
    }

    public void showDeadPlayers(Arena arena) {
        for (Player player : arena.getPlayers()) {
            arena.getPlayers().forEach(player::showPlayer);
        }
    }

    public void hideDeadPlayer(Player player, Arena arena) {
        arena.getAlivePlayers().forEach(alive -> alive.hidePlayer(player));
        arena.getDeadPlayers().forEach(player::showPlayer);
    }


}
