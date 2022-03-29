package me.cubecrafter.woolwars.core;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Arena {

    private final String id;
    private String displayName;
    private GameState gameState;
    private int maxPlayersPerTeam = 1;
    private final Location spawnLocation = Bukkit.getWorld("world").getSpawnLocation();
    private final List<Player> players = new ArrayList<>();
    private final List<Player> spectators = new ArrayList<>();
    private final HashMap<String, Team> teams = new HashMap<>();

    public Arena(String id, YamlConfiguration arenaConfig) {
        this.id = id;
        gameState = GameState.WAITING;
        for (String key : arenaConfig.getConfigurationSection("teams").getKeys(false)) {
            Location location = TextUtil.deserializeLocation(arenaConfig.getString("teams." + key + ".spawn-location"));
            Team team = new Team(key, Color.RED, location);
            teams.put(key, team);
        }
    }

    public String getId() {
        return id;
    }

    public void addPlayer(Player player) {
        players.add(player);
        player.sendMessage(TextUtil.color("&7Joined arena &a" + id));
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<Player> getSpectators() {
        return spectators;
    }

    public int getMaxPlayersPerTeam() {
        return maxPlayersPerTeam;
    }

}
