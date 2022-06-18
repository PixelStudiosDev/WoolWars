package me.cubecrafter.woolwars.api;

import me.cubecrafter.woolwars.api.arena.Arena;
import me.cubecrafter.woolwars.api.database.PlayerData;
import org.bukkit.entity.Player;

import java.util.List;

public interface WoolWarsAPI {

    /**
     * Get all loaded arenas
     * @return all arenas
     */
    List<Arena> getArenas();

    /**
     * Get all arenas of a given group
     * @param group
     * @return arenas
     */
    List<Arena> getArenasByGroup(String group);

    /**
     * Get an arena by player
     * @param player
     * @return the player arena, or null
     */
    Arena getArenaByPlayer(Player player);

    /**
     * Check if a player is playing
     * @param player
     * @return true if the player is playing
     */
    boolean isPlaying(Player player);

    /**
     * Get the player data of an online player
     * @param player
     * @return the player data
     */
    PlayerData getPlayerData(Player player);


}
