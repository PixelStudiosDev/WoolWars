package me.cubecrafter.woolwars.arena;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.Titles;
import lombok.Getter;
import lombok.Setter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.tasks.ArenaPlayingTask;
import me.cubecrafter.woolwars.arena.tasks.ArenaPreRoundTask;
import me.cubecrafter.woolwars.arena.tasks.ArenaRoundOverTask;
import me.cubecrafter.woolwars.arena.tasks.ArenaStartingTask;
import me.cubecrafter.woolwars.utils.Cuboid;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Arena {

    private final String id;
    private final String displayName;
    private final Location lobbyLocation;
    private final int maxPlayersPerTeam;
    private final int minPlayers;
    private final int requiredPoints;
    private final int maxRounds;
    private final List<Player> players = new ArrayList<>();
    private final List<Player> spectators = new ArrayList<>();
    private final List<Player> deadPlayers = new ArrayList<>();
    private final List<Block> placedBlocks = new ArrayList<>();
    private final List<Team> teams = new ArrayList<>();
    private final List<Location> jumpPads = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();
    private final Cuboid blocksRegion;
    private final Cuboid arenaRegion;
    private ArenaStartingTask startingTask;
    private ArenaPlayingTask playingTask;
    private ArenaPreRoundTask preRoundTask;
    private ArenaRoundOverTask roundOverTask;
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
        maxRounds = arenaConfig.getInt("max-rounds");
        for (String key : arenaConfig.getConfigurationSection("teams").getKeys(false)) {
            Location spawn = TextUtil.deserializeLocation(arenaConfig.getString("teams." + key + ".spawn-location"));
            Location barrier1 = TextUtil.deserializeLocation(arenaConfig.getString("teams." + key + ".barrier.point1"));
            Location barrier2 = TextUtil.deserializeLocation(arenaConfig.getString("teams." + key + ".barrier.point2"));
            TeamColor color = TeamColor.valueOf(arenaConfig.getString("teams." + key + ".color"));
            Team team = new Team(key, this, spawn, color, new Cuboid(barrier1, barrier2));
            teams.add(team);
        }
        Location point1 = TextUtil.deserializeLocation(arenaConfig.getString("block-region.point1"));
        Location point2 = TextUtil.deserializeLocation(arenaConfig.getString("block-region.point2"));
        blocksRegion = new Cuboid(point1, point2);
        Location point3 = TextUtil.deserializeLocation(arenaConfig.getString("arena-region.point1"));
        Location point4 = TextUtil.deserializeLocation(arenaConfig.getString("arena-region.point2"));
        arenaRegion = new Cuboid(point3, point4);
        new PowerUp(TextUtil.deserializeLocation("world:4.00:80.00:10.00:0:0"), this);
        killEntities();
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
        if (!getGameState().equals(GameState.WAITING) && !getGameState().equals(GameState.STARTING)) {
            player.sendMessage(TextUtil.color("&cThe game is already started!"));
            return;
        }
        players.add(player);
        player.teleport(lobbyLocation);
        player.setGameMode(GameMode.SURVIVAL);
        player.setFoodLevel(20);
        player.setHealth(20);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        sendMessage(TextUtil.color("&b{player} &ejoined the game! &7({currentplayers}/{maxplayers})"
                .replace("{player}", player.getName())
                .replace("{currentplayers}", String.valueOf(players.size()))
                .replace("{maxplayers}", String.valueOf(getTeams().size()*getMaxPlayersPerTeam()))));
        if (getGameState().equals(GameState.WAITING) && getPlayers().size() >= getMinPlayers()) {
            setGameState(GameState.STARTING);
        }
    }

    public void removePlayer(Player player) {
        players.remove(player);
        Team playerTeam = getTeamByPlayer(player);
        if (playerTeam != null) {
            playerTeam.removeMember(player);
        }
        player.setPlayerListName(player.getName());
        player.setDisplayName(player.getName());
        player.getInventory().setArmorContents(null);
        player.teleport(TextUtil.deserializeLocation(WoolWars.getInstance().getFileManager().getConfig().getString("lobby-location")));
        sendMessage(player.getName() + " has left! (" + getPlayers().size() + "/" + getMaxPlayersPerTeam()*getTeams().size() + ")");
        if (getGameState().equals(GameState.STARTING) && getPlayers().size() < getMinPlayers()) {
            setGameState(GameState.WAITING);
            startingTask.getTask().cancel();
            sendMessage(TextUtil.color("&cNot enough players! Countdown stopped!"));
        }
        if (!gameState.equals(GameState.WAITING) && !gameState.equals(GameState.STARTING) && getTeams().stream().filter(team -> team.getMembers().size() == 0).count() > getTeams().size() - 2) {
            TextUtil.info("Not enough players in arena " + id + ". Restarting...");
            restart();
        }
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        switch (gameState) {
            case WAITING:
                if (startingTask.getTask() != null) startingTask.getTask().cancel();
                if (roundOverTask.getTask() != null) roundOverTask.getTask().cancel();
                if (playingTask.getTask() != null) playingTask.getTask().cancel();
                if (preRoundTask.getTask() != null) preRoundTask.getTask().cancel();
                break;
            case STARTING:
                startingTask = new ArenaStartingTask(this);
                break;
            case PRE_ROUND:
                preRoundTask = new ArenaPreRoundTask(this);
                break;
            case PLAYING:
                playingTask = new ArenaPlayingTask(this);
                break;
            case ROUND_OVER:
                roundOverTask = new ArenaRoundOverTask(this);
                break;
        }
    }

    public void assignTeams() {
        for (Player player : getPlayers()) {
            Team minPlayers = getTeams().stream().min(Comparator.comparing(team -> team.getMembers().size())).orElse(getTeams().get(0));
            minPlayers.addMember(player);
        }
    }

    public List<Player> getAlivePlayers() {
        return getPlayers().stream().filter(player -> !deadPlayers.contains(player)).collect(Collectors.toList());
    }

    public Team getTeamByName(String name) {
        return teams.stream().filter(team -> team.getName().equals(name)).findAny().orElse(null);
    }

    public Team getTeamByPlayer(Player player) {
        return getTeams().stream().filter(team -> team.getMembers().contains(player)).findAny().orElse(null);
    }

    public boolean isTeammate(Player player, Player other) {
        return getTeamByPlayer(player).getMembers().contains(other);
    }

    public void restart() {
        setGameState(GameState.WAITING);
        getTeams().forEach(team -> {
            team.getMembers().clear();
            team.resetPoints();
        });
        new ArrayList<>(getPlayers()).forEach(this::removePlayer);
        setRound(0);
    }

    public String getTimerFormatted() {
        int minutes = (timer / 60) % 60;
        int seconds = (timer) % 60;
        return (minutes > 9 ? minutes : "0" + minutes) + ":" + (seconds > 9 ? seconds : "0" + seconds);
    }

    public void killEntities() {
        for (Entity entity : arenaRegion.getWorld().getEntities()) {
            if (entity.getType().equals(EntityType.ITEM_FRAME) || entity.getType().equals(EntityType.ARMOR_STAND) || entity.getType().equals(EntityType.PLAYER) || entity.getType().equals(EntityType.PAINTING)) continue;
            if (arenaRegion.isInside(entity.getLocation())) {
                entity.remove();
            }
        }
    }

    public boolean isLastRound() {
        return round == maxRounds;
    }

    public void sendMessage(String msg) {
        getPlayers().forEach(player -> player.sendMessage(TextUtil.color(msg)));
    }

    public void sendTitle(int stay, String title, String subtitle) {
        getPlayers().forEach(player -> Titles.sendTitle(player, 0, stay, 0, TextUtil.color(title), TextUtil.color(subtitle)));
    }

    public void playSound(String sound) {
        getPlayers().forEach(player -> XSound.play(player, sound));
    }

}
