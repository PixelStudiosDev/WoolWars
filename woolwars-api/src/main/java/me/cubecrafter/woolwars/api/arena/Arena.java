package me.cubecrafter.woolwars.api.arena;

import me.cubecrafter.woolwars.api.team.Team;
import org.bukkit.entity.Player;

import java.util.List;

public interface Arena {

    void addPlayer(Player player);

    void forceStart();

    void removePlayer(Player player, boolean teleportToLobby);

    void removeAllPlayers();

    void setGameState(GameState gameState);

    void restart();

    void assignTeams();

    List<Player> getAlivePlayers();

    Team getTeamByName(String name);

    Team getTeamByPlayer(Player player);

    boolean isTeammate(Player player, Player other);

    boolean isDead(Player player);

    boolean isAlive(Player player);

    String getTimerFormatted();

    void resetBlocks();

    String getTeamPointsFormatted();

    void addKills(Player player, int n);

    void addDeaths(Player player, int n);

    void addPlacedWool(Player player, int n);

    void addBrokenBlocks(Player player, int n);


}
