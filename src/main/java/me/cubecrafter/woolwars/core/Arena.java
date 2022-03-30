package me.cubecrafter.woolwars.core;

import lombok.Getter;
import me.cubecrafter.woolwars.core.tasks.ArenaStartingTask;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
public class Arena {

    private final String id;
    private final String displayName;
    private final Location lobbyLocation;
    private final int maxPlayersPerTeam;
    private final int minPlayers;
    private final List<Player> players = new ArrayList<>();
    private final List<Player> spectators = new ArrayList<>();
    private final HashMap<String, Team> teams = new HashMap<>();
    private BukkitTask startingTask;
    private GameState gameState = GameState.WAITING;

    public Arena(String id, YamlConfiguration arenaConfig) {
        this.id = id;
        lobbyLocation = TextUtil.deserializeLocation(arenaConfig.getString("lobby-location"));
        displayName = TextUtil.color(arenaConfig.getString("displayname"));
        maxPlayersPerTeam = arenaConfig.getInt("max-players-per-team");
        minPlayers = arenaConfig.getInt("min-players");
        for (String key : arenaConfig.getConfigurationSection("teams").getKeys(false)) {
            Location spawn = TextUtil.deserializeLocation(arenaConfig.getString("teams." + key + ".spawn-location"));
            Team team = new Team(key, spawn, Color.RED);
            teams.put(key, team);
        }
    }

    public void addPlayer(Player player) {
        players.add(player);
        player.teleport(lobbyLocation);
        broadcast(TextUtil.color("&b{player} &ejoined the game! &7({currentplayers}/{maxplayers})"
                .replace("{player}", player.getName())
                .replace("{currentplayers}", String.valueOf(players.size()))
                .replace("{maxplayers}", String.valueOf(getTeams().size()*getMaxPlayersPerTeam()))));
        if (getGameState().equals(GameState.WAITING) && getPlayers().size() >= getMinPlayers()) {
            setGameState(GameState.STARTING);
        }
    }

    public void removePlayer(Player player) {
        players.remove(player);
        broadcast(player.getName() + " has left! (" + getPlayers().size() + "/" + getMaxPlayersPerTeam()*getTeams().size() + ")");
        if (getGameState().equals(GameState.STARTING) && getPlayers().size() < getMinPlayers()) {
            setGameState(GameState.WAITING);
            startingTask.cancel();
            broadcast(TextUtil.color("&cNot enough players! Countdown stopped!"));
        }
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        switch (getGameState()) {
            case WAITING:
                break;
            case STARTING:
                startingTask = new ArenaStartingTask(this).getTask();
                break;
            case PLAYING:
                break;
            case RESTARTING:
                break;
        }
    }

    private void assignTeams() {
        for (Player player : getPlayers()) {
            Team team = new ArrayList<>(getTeams().values()).get(0);
            for (Team t : getTeams().values()) {
                if (t.getMembers().size() < team.getMembers().size() && t.getMembers().size() < getMaxPlayersPerTeam()) {
                    team = t;
                }
            }
            team.addMember(player);
        }
    }

    public void broadcast(String msg) {
        for (Player player : getPlayers()) {
            player.sendMessage(TextUtil.color(msg));
        }
    }

}
