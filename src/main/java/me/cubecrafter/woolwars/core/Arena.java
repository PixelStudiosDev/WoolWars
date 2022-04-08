package me.cubecrafter.woolwars.core;

import lombok.Getter;
import lombok.Setter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.core.tasks.ArenaPlayingTask;
import me.cubecrafter.woolwars.core.tasks.ArenaSelectKitTask;
import me.cubecrafter.woolwars.core.tasks.ArenaStartingTask;
import me.cubecrafter.woolwars.utils.Cuboid;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

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
    private final int requiredPoints;
    private final List<Player> players = new ArrayList<>();
    private final List<Player> spectators = new ArrayList<>();
    private final List<Player> deadPlayers = new ArrayList<>();
    private final HashMap<String, Team> teams = new HashMap<>();
    private final Cuboid blocksRegion;
    private ArenaStartingTask startingTask;
    private ArenaPlayingTask playingTask;
    private ArenaSelectKitTask selectKitTask;
    private GameState gameState = GameState.WAITING;
    private boolean enabled = true;
    @Setter private int round = 0;
    @Setter private int timer = 0;

    public Arena(String id, YamlConfiguration arenaConfig) {
        this.id = id;
        lobbyLocation = TextUtil.deserializeLocation(arenaConfig.getString("lobby-location"));
        displayName = TextUtil.color(arenaConfig.getString("displayname"));
        maxPlayersPerTeam = arenaConfig.getInt("max-players-per-team");
        minPlayers = arenaConfig.getInt("min-players");
        requiredPoints = arenaConfig.getInt("required-points-to-win");
        for (String key : arenaConfig.getConfigurationSection("teams").getKeys(false)) {
            Location spawn = TextUtil.deserializeLocation(arenaConfig.getString("teams." + key + ".spawn-location"));
            TeamColor color = TeamColor.valueOf(arenaConfig.getString("teams." + key + ".color"));
            Team team = new Team(key, spawn, color);
            teams.put(key, team);
        }
        Location point1 = TextUtil.deserializeLocation(arenaConfig.getString("block-region.point1"));
        Location point2 = TextUtil.deserializeLocation(arenaConfig.getString("block-region.point2"));
        blocksRegion = new Cuboid(point1, point2);
    }

    public void addPlayer(Player player) {
        if (!isEnabled()) {
            player.sendMessage(TextUtil.color("&cThis arena is currently disabled!"));
            return;
        }
        if (getPlayers().contains(player)) {
            player.sendMessage(TextUtil.color("&cYou are already in this arena!"));
            return;
        }
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
        player.teleport(TextUtil.deserializeLocation(WoolWars.getInstance().getFileManager().getConfig().getString("lobby-location")));
        broadcast(player.getName() + " has left! (" + getPlayers().size() + "/" + getMaxPlayersPerTeam()*getTeams().size() + ")");
        if (getGameState().equals(GameState.STARTING) && getPlayers().size() < getMinPlayers()) {
            setGameState(GameState.WAITING);
            startingTask.getTask().cancel();
            broadcast(TextUtil.color("&cNot enough players! Countdown stopped!"));
        }
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        switch (gameState) {
            case WAITING:
                break;
            case STARTING:
                startingTask = new ArenaStartingTask(this);
                break;
            case PRE_ROUND:
                selectKitTask = new ArenaSelectKitTask(this);
                break;
            case PLAYING:
                playingTask = new ArenaPlayingTask(this);
                break;
            case RESTARTING:
                break;
        }
    }

    public void assignTeams() {
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

    public Team getTeamByName(String name) {
        return teams.get(name);
    }

    public Team getTeamByPlayer(Player player) {
        for (Team team : getTeams()) {
            if (team.getMembers().contains(player)) {
                return team;
            }
        }
        return null;
    }

    public Team getTeamByWool(Material wool) {
        for (Team team : getTeams()) {
            if (team.getTeamColor().getWoolMaterial().equals(wool)) {
                return team;
            }
        }
        return null;
    }

    public void broadcast(String msg) {
        for (Player player : getPlayers()) {
            player.sendMessage(TextUtil.color(msg));
        }
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    public List<Team> getTeams() {
        return new ArrayList<>(teams.values());
    }

    public void restart() {
        getTeams().forEach(team -> {
            team.getMembers().clear();
            team.resetPoints();
        });
        new ArrayList<>(getPlayers()).forEach(this::removePlayer);
        setGameState(GameState.WAITING);
        setRound(0);
    }

    public String getTimerFormatted() {
        int minutes = (timer / 60) % 60;
        int seconds = (timer) % 60;
        return (minutes > 9 ? minutes : "0" + minutes) + ":" + (seconds > 9 ? seconds : "0" + seconds);
    }

}
