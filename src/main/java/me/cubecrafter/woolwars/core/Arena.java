package me.cubecrafter.woolwars.core;

import me.cubecrafter.woolwars.core.tasks.ArenaStartingTask;
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
    private Location lobby = Bukkit.getWorld("world").getSpawnLocation();
    private int maxPlayersPerTeam = 1;
    private int minPlayers = 1;
    private final List<Player> players = new ArrayList<>();
    private final List<Player> spectators = new ArrayList<>();
    private final HashMap<String, Team> teams = new HashMap<>();
    private BukkitTask startingTask;

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
        player.teleport(lobby);
        broadcast(TextUtil.color("&b{player} &ejoined the game! &7({currentplayers}/{maxplayers})"
                .replace("{player}", player.getName())
                .replace("{currentplayers}", String.valueOf(players.size()))
                .replace("{maxplayers}", String.valueOf(getTeams().size()*getMaxPlayersPerTeam()))));
        if (getPlayers().size() >= getMinPlayers()) {
            startingTask = new ArenaStartingTask(this).getTask();
        }
    }

    public void removePlayer(Player player) {
        players.remove(player);
        broadcast(player.getName() + " has left! (" + getPlayers().size() + "/" + getMaxPlayersPerTeam()*getTeams().size() + ")");
        if (getPlayers().size() < getMinPlayers()) {
            broadcast(TextUtil.color("&cNot enough players! Countdown stopped!"));
            startingTask.cancel();
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Location getLobbyLocation() {
        return lobby;
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

    public int getMinPlayers() {
        return minPlayers;
    }

    private void assignTeams() {
        for (Player player : getPlayers()) {
            Team team = getTeams().get(0);
            for (Team t : getTeams()) {
                if (t.getMembers().size() < team.getMembers().size() && t.getMembers().size() < getMaxPlayersPerTeam()) {
                    team = t;
                }
            }
            team.addMember(player);
        }
    }

    public List<Team> getTeams() {
        return new ArrayList<>(teams.values());
    }

    public void broadcast(String msg) {
        for (Player player : getPlayers()) {
            player.sendMessage(TextUtil.color(msg));
        }
    }

}
