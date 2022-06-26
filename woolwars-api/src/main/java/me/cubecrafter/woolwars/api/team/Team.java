package me.cubecrafter.woolwars.api.team;

import me.cubecrafter.woolwars.api.arena.Arena;
import me.cubecrafter.woolwars.api.arena.Cuboid;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public interface Team {

    /**
     * Get all members of this team
     * @return The team members
     */
    List<Player> getMembers();

    /**
     * Get the team name
     * @return The team name
     */
    String getName();

    /**
     * Get the team arena
     * @return The team arena
     */
    Arena getArena();

    /**
     * Get the team spawn location
     * @return The team spawn location
     */
    Location getSpawnLocation();

    /**
     * Get the team color
     * @return The team color
     */
    TeamColor getTeamColor();

    /**
     * Get the team barrier
     * @return The team barrier
     */
    Cuboid getBarrier();

    /**
     * Get the team base
     * @return The team base
     */
    Cuboid getBase();

    /**
     * Get the team points
     * @return The team points
     */
    int getPoints();

    /**
     * Get the first letter of the team name capitalized
     * @return The capitalized first letter of the team name
     */
    String getTeamLetter();

    /**
     * Add a member to this team
     * @param player The player to add
     */
    void addMember(Player player);

    /**
     * Remove a member from this team
     * @param player The player to remove
     */
    void removeMember(Player player);

    /**
     * Apply the team name tags to all members
     */
    void applyNameTags();

    /**
     * Teleport all members to the team spawn location
     */
    void teleportToSpawn();

    /**
     * Add a point to this team
     */
    void addPoint();

    /**
     * Remove the team barrier
     */
    void removeBarrier();

    /**
     * Spawn the team barrier
     */
    void spawnBarrier();

    /**
     * Reset the team members, points and name tags
     */
    void reset();

}
