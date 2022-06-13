package me.cubecrafter.woolwars.game.arena;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import lombok.Setter;
import me.cubecrafter.woolwars.api.game.arena.GameState;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.game.powerup.PowerUp;
import me.cubecrafter.woolwars.game.tasks.*;
import me.cubecrafter.woolwars.game.team.Team;
import me.cubecrafter.woolwars.game.team.TeamColor;
import me.cubecrafter.woolwars.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class Arena implements Runnable {

    private final String id;
    private final String displayName;
    private final String group;
    private final Location lobbyLocation;
    private final int maxPlayersPerTeam;
    private final int minPlayers;
    private final int requiredPoints;
    private final int maxRounds;
    private final List<Player> players = new ArrayList<>();
    private final List<Player> deadPlayers = new ArrayList<>();
    private final List<Block> arenaPlacedBlocks = new ArrayList<>();
    private final List<Team> teams = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();
    private final Map<Player, Integer> kills = new HashMap<>();
    private final Map<Player, Integer> deaths = new HashMap<>();
    private final Map<Player, Integer> placedBlocks = new HashMap<>();
    private final Map<Player, Integer> brokenBlocks = new HashMap<>();
    private final Cuboid woolRegion;
    private final Cuboid arenaRegion;
    private ArenaStartingTask startingTask;
    private ArenaPlayingTask playingTask;
    private ArenaPreRoundTask preRoundTask;
    private ArenaRoundOverTask roundOverTask;
    private ArenaGameEndedTask gameEndedTask;
    private GameState gameState = GameState.WAITING;
    @Setter private int round = 0;
    @Setter private int timer = 0;
    @Setter private boolean centerLocked = false;

    /**
     * Creates a new arena.
     * @param id
     * @param arenaConfig
     */
    public Arena(String id, YamlConfiguration arenaConfig) {
        this.id = id;
        group = arenaConfig.getString("group");
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
            Location base1 = TextUtil.deserializeLocation(arenaConfig.getString("teams." + key + ".base.point1"));
            Location base2 = TextUtil.deserializeLocation(arenaConfig.getString("teams." + key + ".base.point2"));
            TeamColor color = TeamColor.valueOf(arenaConfig.getString("teams." + key + ".color"));
            Team team = new Team(key, this, spawn, color, new Cuboid(barrier1, barrier2), new Cuboid(base1, base2));
            teams.add(team);
        }
        Location point1 = TextUtil.deserializeLocation(arenaConfig.getString("block-region.point1"));
        Location point2 = TextUtil.deserializeLocation(arenaConfig.getString("block-region.point2"));
        woolRegion = new Cuboid(point1, point2);
        Location point3 = TextUtil.deserializeLocation(arenaConfig.getString("arena-region.point1"));
        Location point4 = TextUtil.deserializeLocation(arenaConfig.getString("arena-region.point2"));
        arenaRegion = new Cuboid(point3, point4);
        for (String line : arenaConfig.getStringList("powerup-locations")) {
            Location location = TextUtil.deserializeLocation(line);
            PowerUp powerUp = new PowerUp(location, this);
            powerUps.add(powerUp);
        }
        killEntities();
    }

    /**
     * Adds a player to the arena.
     * @param player
     */
    public void addPlayer(Player player) {
        if (players.contains(player)) {
            player.sendMessage(TextUtil.color("&cYou are already in this game!"));
            return;
        }
        if (!gameState.equals(GameState.WAITING) && !gameState.equals(GameState.STARTING)) {
            player.sendMessage(TextUtil.color("&cThe game is already started!"));
            return;
        }
        if (players.size() >= maxPlayersPerTeam * teams.size()) {
            player.sendMessage(TextUtil.color("&cThis game is full!"));
            return;
        }
        players.add(player);
        player.teleport(lobbyLocation);
        player.setGameMode(GameMode.ADVENTURE);
        player.setFoodLevel(20);
        player.setHealth(20);
        player.getInventory().setArmorContents(null);
        player.getInventory().clear();
        player.setFlying(false);
        player.setAllowFlight(false);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (players.contains(online)) {
                online.showPlayer(player);
                player.showPlayer(online);
            } else {
                online.hidePlayer(player);
                player.hidePlayer(online);
            }
        }
        ItemStack leaveItem = new ItemBuilder("RED_BED").setDisplayName("&cReturn to Lobby &7(Right-Click)").setLore(Arrays.asList("&7Click to return to the lobby!")).setTag("leave-item").build();
        player.getInventory().setItem(8, leaveItem);
        TextUtil.sendMessage(players, "&e{player} &7joined the game! &8({currentplayers}/{maxplayers})"
                .replace("{player}", player.getName())
                .replace("{currentplayers}", String.valueOf(players.size()))
                .replace("{maxplayers}", String.valueOf(getTeams().size() * maxPlayersPerTeam)));
        if (gameState.equals(GameState.WAITING) && getPlayers().size() >= getMinPlayers()) {
            setGameState(GameState.STARTING);
        }
    }

    /**
     * Force starts the game.
     */
    public void forceStart() {
        if (gameState.equals(GameState.WAITING)) setGameState(GameState.STARTING);
    }

    public void removePlayer(Player player, boolean teleportToLobby) {
        players.remove(player);
        Team playerTeam = getTeamByPlayer(player);
        if (playerTeam != null) {
            PlayerScoreboard scoreboard = PlayerScoreboard.getScoreboard(player);
            if (scoreboard != null) {
                scoreboard.removeGamePrefix(playerTeam);
            }
            playerTeam.removeMember(player);
        }
        player.getInventory().setArmorContents(null);
        player.getInventory().clear();
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setFoodLevel(20);
        player.setHealth(20);
        player.setGameMode(GameMode.SURVIVAL);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        if (teleportToLobby) player.teleport(Configuration.LOBBY_LOCATION.getAsLocation());
        ArenaUtil.showLobbyPlayers(player, this);
        if (gameState.equals(GameState.WAITING) || gameState.equals(GameState.STARTING)) {
            TextUtil.sendMessage(players, "&e{player} &7left the game! &8({currentplayers}/{maxplayers}"
                    .replace("{player}", player.getName())
                    .replace("{currentplayers}", String.valueOf(players.size()))
                    .replace("{maxplayers}", String.valueOf(getTeams().size()*getMaxPlayersPerTeam())));
        } else {
            TextUtil.sendMessage(players, "&c{player} &7has left!".replace("{player}", player.getDisplayName()));
        }
        if (gameState.equals(GameState.STARTING) && getPlayers().size() < getMinPlayers()) {
            startingTask.cancelTask();
            TextUtil.sendMessage(players, "&cWe don't have enough players! Start cancelled!");
            setGameState(GameState.WAITING);
        }
        if (!gameState.equals(GameState.WAITING) && !gameState.equals(GameState.STARTING) && teams.stream().filter(team -> team.getMembers().size() == 0).count() > teams.size() - 2) {
            TextUtil.info("Not enough players in game " + id + ". Restarting...");
            restart();
        }
    }

    public void removeAllPlayers() {
        for (Player player : players) {
            player.getInventory().setArmorContents(null);
            player.getInventory().clear();
            player.setFlying(false);
            player.setAllowFlight(false);
            player.setFoodLevel(20);
            player.setHealth(20);
            player.setGameMode(GameMode.SURVIVAL);
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
            ArenaUtil.showLobbyPlayers(player, this);
            player.teleport(Configuration.LOBBY_LOCATION.getAsLocation());
        }
        players.clear();
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        switch (gameState) {
            case STARTING:
                startingTask = new ArenaStartingTask(this, 10);
                break;
            case PRE_ROUND:
                preRoundTask = new ArenaPreRoundTask(this, 10);
                break;
            case ACTIVE_ROUND:
                playingTask = new ArenaPlayingTask(this, 60);
                break;
            case ROUND_OVER:
                roundOverTask = new ArenaRoundOverTask(this, 5);
                break;
            case GAME_ENDED:
                gameEndedTask = new ArenaGameEndedTask(this, 10);
                break;
        }
    }

    public void restart() {
        cancelTasks();
        removeAllPlayers();
        getTeams().forEach(Team::reset);
        resetBlocks();
        powerUps.forEach(PowerUp::remove);
        setRound(0);
        setTimer(0);
        killEntities();
        deadPlayers.clear();
        kills.clear();
        deaths.clear();
        placedBlocks.clear();
        brokenBlocks.clear();
        setGameState(GameState.WAITING);
    }

    public void assignTeams() {
        for (Player player : players) {
            Team minPlayers = getTeams().stream().min(Comparator.comparing(team -> team.getMembers().size())).orElse(teams.get(new Random().nextInt(teams.size())));
            minPlayers.addMember(player);
        }
    }

    public List<Player> getAlivePlayers() {
        return players.stream().filter(player -> !deadPlayers.contains(player)).collect(Collectors.toList());
    }

    public Team getTeamByName(String name) {
        return teams.stream().filter(team -> team.getName().equals(name)).findAny().orElse(null);
    }

    public Team getTeamByPlayer(Player player) {
        return teams.stream().filter(team -> team.getMembers().contains(player)).findAny().orElse(null);
    }

    public boolean isTeammate(Player player, Player other) {
        return getTeamByPlayer(player).getMembers().contains(other);
    }

    public boolean isDead(Player player) {
        return deadPlayers.contains(player);
    }

    public boolean isAlive(Player player) {
        return !deadPlayers.contains(player);
    }

    public String getTimerFormatted() {
        int minutes = (timer / 60) % 60;
        int seconds = (timer) % 60;
        return (minutes > 9 ? minutes : "0" + minutes) + ":" + (seconds > 9 ? seconds : "0" + seconds);
    }

    public void killEntities() {
        for (Entity entity : arenaRegion.getWorld().getEntities()) {
            if (entity.getType().equals(EntityType.ITEM_FRAME) || entity.getType().equals(EntityType.ARMOR_STAND) || entity.getType().equals(EntityType.PAINTING) || entity.getType().equals(EntityType.PLAYER)) continue;
            if (arenaRegion.isInside(entity.getLocation())) {
                entity.remove();
            }
        }
    }

    public void resetBlocks() {
        List<String> defaultBlocks = new ArrayList<>(Arrays.asList("QUARTZ_BLOCK", "SNOW_BLOCK", "WHITE_WOOL"));
        arenaPlacedBlocks.forEach(block -> block.setType(Material.AIR));
        arenaPlacedBlocks.clear();
        Random random = new Random();
        woolRegion.getBlocks().forEach(block -> block.setType(XMaterial.matchXMaterial(defaultBlocks.get(random.nextInt(defaultBlocks.size()))).get().parseMaterial()));
    }

    public void respawnPlayers() {
        for (Player player : players) {
            player.setGameMode(GameMode.SURVIVAL);
            player.setFlying(false);
            player.setAllowFlight(false);
            player.setHealth(20);
            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        }
        deadPlayers.clear();
        teams.forEach(Team::teleportToSpawn);
        for (Player player : players) {
            players.forEach(player::showPlayer);
        }
    }

    public void cancelTasks() {
        if (startingTask != null) startingTask.cancelTask();
        if (roundOverTask != null) roundOverTask.cancelTask();
        if (playingTask != null) playingTask.cancelTask();
        if (preRoundTask != null) preRoundTask.cancelTask();
        if (gameEndedTask != null) gameEndedTask.cancelTask();
    }

    public boolean isLastRound() {
        return round == maxRounds;
    }

    public String getTeamPointsFormatted() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < teams.size(); i++) {
            Team team = teams.get(i);
            if (i > 0) {
                builder.append(" &7- ");
            }
            builder.append(team.getTeamColor().getChatColor()).append(team.getPoints());
        }
        return TextUtil.color(builder.toString());
    }

    public void addKills(Player player, int n) {
        kills.merge(player, n, Integer::sum);
    }

    public void addDeaths(Player player, int n) {
        deaths.merge(player, n, Integer::sum);
    }

    public void addPlacedBlocks(Player player, int n) {
        placedBlocks.merge(player, n, Integer::sum);
    }

    public void addBrokenBlocks(Player player, int n) {
        brokenBlocks.merge(player, n, Integer::sum);
    }

    @Override
    public void run() {

    }

}
