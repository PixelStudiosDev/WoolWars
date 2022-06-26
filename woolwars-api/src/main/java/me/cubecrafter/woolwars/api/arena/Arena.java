package me.cubecrafter.woolwars.api.arena;

import me.cubecrafter.woolwars.api.powerup.PowerUp;
import me.cubecrafter.woolwars.api.team.Team;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public interface Arena {

    /**
     * Get the arena configuration
     * @return The arena configuration
     */
    YamlConfiguration getConfig();

    /**
     * Get the arena ID
     * @return The arena ID
     */
    String getId();

    /**
     * Get the arena displayname
     * @return The arena displayname
     */
    String getDisplayName();

    /**
     * Get the arena group
     * @return The arena group
     */
    String getGroup();

    /**
     * Get the arena lobby location
     * @return The arena lobby location
     */
    Location getLobby();

    /**
     * Get the maximum number of players per team
     * @return Number of players per team
     */
    int getMaxPlayersPerTeam();

    /**
     * Get the minimum number of players required to start the game
     * @return Minimum number of players
     */
    int getMinPlayers();

    /**
     * Get the required points to win the game
     * @return Required points
     */
    int getWinPoints();

    /**
     * Get the players in the arena
     * @return The players in the arena
     */
    List<Player> getPlayers();

    /**
     * Get the dead players in the arena
     * @return The dead players in the arena
     */
    List<Player> getDeadPlayers();

    /**
     * Get the alive players in the arena
     * @return The alive players in the arena
     */
    List<Player> getAlivePlayers();

    /**
     * Get the placed blocks in the arena
     * @return The placed blocks in the arena
     */
    List<Block> getPlacedBlocks();

    /**
     * Get the arena teams
     * @return The arena teams
     */
    List<Team> getTeams();

    /**
     * Get the arena powerups
     * @return The arena powerups
     */
    List<PowerUp> getPowerUps();

    /**
     * Get the player kill count
     * @param player The player
     * @return The player kill count
     */
    int getKills(Player player);

    /**
     * Add kills to a player
     * @param player The player
     * @param kills The kills to add
     */
    void addKills(Player player, int kills);

    /**
     * Get the player death count
     * @param player The player
     * @return The player death count
     */
    int getDeaths(Player player);

    /**
     * Add deaths to a player
     * @param player The player
     * @param deaths The deaths to add
     */
    void addDeaths(Player player, int deaths);

    /**
     * Get the number of wool placed by a player
     * @param player The player
     * @return The number of wool placed
     */
    int getWoolPlaced(Player player);

    /**
     * Add wool placed to a player
     * @param player The player
     * @param woolPlaced The number of wool placed to add
     */
    void addWoolPlaced(Player player, int woolPlaced);

    /**
     * Get the number of blocks broken by a player
     * @param player The player
     * @return The number of blocks broken
     */
    int getBlocksBroken(Player player);

    /**
     * Add blocks broken to a player
     * @param player The player
     * @param blocksBroken The number of blocks broken to add
     */
    void addBlocksBroken(Player player, int blocksBroken);

    /**
     * Get the center region of the arena
     * @return The center region
     */
    Cuboid getCenter();

    /**
     * Get the region of the arena
     * @return The arena region
     */
    Cuboid getArenaRegion();

    /**
     * Get the current game state
     * @return The current game state
     */
    GameState getGameState();

    /**
     * Get the current round
     * @return The current round
     */
    int getRound();

    /**
     * Get the current time left in seconds
     * @return The current time left
     */
    int getTimer();

    /**
     * Check if the center is locked
     * @return True if the center is locked, false otherwise
     */
    boolean isCenterLocked();

    /**
     * Get the current time left in the format MM:SS
     * @return The current time left
     */
    String getTimerFormatted();

    /**
     * Add a player to the arena
     * @param player The player to add
     */
    void addPlayer(Player player);

    /**
     * Remove a player from the arena
     * @param player The player to remove
     * @param teleportToLobby True if the player should be teleported to the lobby, false otherwise
     */
    void removePlayer(Player player, boolean teleportToLobby);

    /**
     * Force start the game
     */
    void forceStart();

    /**
     * Set the arena game state
     * @param gameState The game state to set
     */
    void setGameState(GameState gameState);

    /**
     * Set the arena round
     * @param round The round to set
     */
    void setRound(int round);

    /**
     * Set the arena timer
     * @param seconds The time to set in seconds
     */
    void setTimer(int seconds);

    /**
     * Set the center locked status
     * @param locked True to lock the center, false to unlock it
     */
    void setCenterLocked(boolean locked);

    /**
     * Restart the arena
     */
    void restart();

    /**
     * Get a team by name
     * @param name The team name
     * @return The team
     */
    Team getTeamByName(String name);

    /**
     * Get a team by player
     * @param player The player
     * @return The team
     */
    Team getTeamByPlayer(Player player);

    /**
     * Check if a player is teammate of another player
     * @param player The first player
     * @param other The second player
     * @return True if the players are teammates, false otherwise
     */
    boolean isTeammate(Player player, Player other);

    /**
     * Check if a player is dead
     * @param player The player
     * @return True if the player is dead, false otherwise
     */
    boolean isDead(Player player);

    /**
     * Check if a player is alive
     * @param player The player
     * @return True if the player is alive, false otherwise
     */
    boolean isAlive(Player player);

    /**
     * Remove all blocks placed by a player
     */
    void removePlacedBlocks();

    /**
     * Fill the center region
     */
    void fillCenter();

    /**
     * Get all the team points in a formatted string
     * @return The team points
     */
    String getPointsFormatted();

    /**
     * Cancel all running tasks
     */
    void cancelTasks();

}
