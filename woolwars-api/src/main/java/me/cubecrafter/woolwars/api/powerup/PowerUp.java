package me.cubecrafter.woolwars.api.powerup;

import me.cubecrafter.woolwars.api.arena.Arena;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface PowerUp {

    /**
     * Get the powerup location
     * @return The powerup location
     */
    Location getLocation();

    /**
     * Get the powerup arena
     * @return The powerup arena
     */
    Arena getArena();

    /**
     * Get the powerup data
     * @return The powerup data
     */
    PowerUpData getData();

    /**
     * Check if the powerup is active
     * @return True if the powerup is active, false otherwise
     */
    boolean isActive();

    /**
     * Make a player use the powerup
     * @param player The player
     */
    void use(Player player);

    /**
     * Spawn the powerup
     */
    void spawn();

    /**
     * Remove the powerup
     */
    void remove();

    /**
     * Rotate the powerup
     */
    void rotate();

}
