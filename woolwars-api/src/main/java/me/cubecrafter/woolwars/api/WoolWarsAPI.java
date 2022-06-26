package me.cubecrafter.woolwars.api;

import me.cubecrafter.woolwars.api.arena.Arena;
import me.cubecrafter.woolwars.api.database.PlayerData;
import me.cubecrafter.woolwars.api.kits.Kit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public interface WoolWarsAPI {

    /**
     * Get the lobby location
     * @return The lobby location
     */
    Location getLobbyLocation();

    /**
     * Get all arenas
     * @return The arenas
     */
    List<Arena> getArenas();

    /**
     * Get an arena by player
     * @param player The player
     * @return The arena
     */
    Arena getArenaByPlayer(Player player);

    /**
     * Get an arena by id
     * @param id The arena id
     * @return The arena
     */
    Arena getArenaById(String id);

    /**
     * Get all arenas of a given group
     * @param group The arena group
     * @return The arenas
     */
    List<Arena> getArenasByGroup(String group);

    /**
     * Get all arena groups
     * @return The arena groups
     */
    List<String> getGroups();

    /**
     * Get a kit by id
     * @param id The kit id
     * @return The kit
     */
    Kit getKitById(String id);

    /**
     * Get all loaded kits
     * @return The kits
     */
    List<Kit> getKits();

    /**
     * Get the player data of an online player
     * @param player The player
     * @return The player data
     */
    PlayerData getPlayerData(Player player);

    /**
     * Check if a player is playing
     * @param player The player
     * @return True if the player is playing, false otherwise
     */
    boolean isPlaying(Player player);

    /**
     * Join a random arena
     * @param player The player
     */
    void joinRandomArena(Player player);

    /**
     * Join a random arena of a given group
     * @param player The player
     * @param group The arena group
     */
    void joinRandomArena(Player player, String group);

}
