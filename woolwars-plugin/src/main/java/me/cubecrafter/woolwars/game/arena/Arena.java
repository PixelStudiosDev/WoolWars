package me.cubecrafter.woolwars.game.arena;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.Titles;
import lombok.Getter;
import lombok.Setter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.config.ConfigPath;
import me.cubecrafter.woolwars.game.powerup.PowerUp;
import me.cubecrafter.woolwars.game.tasks.*;
import me.cubecrafter.woolwars.game.team.Team;
import me.cubecrafter.woolwars.game.team.TeamColor;
import me.cubecrafter.woolwars.utils.*;
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
public class Arena {

    private final String id;
    private final String displayName;
    private final String group;
    private final Location lobbyLocation;
    private final int maxPlayersPerTeam;
    private final int minPlayers;
    private final int requiredPoints;
    private final int maxRounds;
    private final List<Player> players = new ArrayList<>();
    private final List<Player> spectators = new ArrayList<>();
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
    private GamePhase gamePhase = GamePhase.NONE;
    private ArenaState arenaState = ArenaState.WAITING;
    @Setter private int round = 0;
    @Setter private int timer = 0;

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

    public void addPlayer(Player player) {
        if (players.contains(player)) {
            player.sendMessage(TextUtil.color("&cYou are already in this game!"));
            return;
        }
        if (!arenaState.equals(ArenaState.WAITING) && !arenaState.equals(ArenaState.STARTING)) {
            player.sendMessage(TextUtil.color("&cThe game is already started!"));
            return;
        }
        if (players.size() >= maxPlayersPerTeam * teams.size()) {
            player.sendMessage(TextUtil.color("&cThis game is full!"));
            return;
        }
        players.add(player);
        player.teleport(lobbyLocation);
        player.setGameMode(GameMode.SURVIVAL);
        player.setFoodLevel(20);
        player.setHealth(20);
        player.getInventory().setArmorContents(null);
        player.getInventory().clear();
        player.setFlying(false);
        player.setAllowFlight(false);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        ArenaUtil.hideLobbyPlayers(player, this);
        ItemStack leaveItem = new ItemBuilder("RED_BED").setDisplayName("&cReturn to Lobby").setLore(Arrays.asList("&7Click to return to the lobby!")).setTag("leave-item").build();
        player.getInventory().setItem(8, leaveItem);
        sendMessage(TextUtil.color("&e{player} &7joined the game! &8({currentplayers}/{maxplayers})"
                .replace("{player}", player.getName())
                .replace("{currentplayers}", String.valueOf(players.size()))
                .replace("{maxplayers}", String.valueOf(getTeams().size() * getMaxPlayersPerTeam()))));
        if (arenaState.equals(ArenaState.WAITING) && getPlayers().size() >= getMinPlayers()) {
            setArenaState(ArenaState.STARTING);
        }
    }

    public void forceStart() {
        if (arenaState.equals(ArenaState.WAITING)) setArenaState(ArenaState.STARTING);
    }

    public void removePlayer(Player player, boolean teleportToLobby) {
        players.remove(player);
        Team playerTeam = getTeamByPlayer(player);
        if (playerTeam != null) {
            GameScoreboard scoreboard = GameScoreboard.getScoreboard(player);
            scoreboard.removeGamePrefix(playerTeam);
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
        if (teleportToLobby) player.teleport(ConfigPath.LOBBY_LOCATION.getAsLocation());
        ArenaUtil.showLobbyPlayers(player);
        if (arenaState.equals(ArenaState.WAITING) || arenaState.equals(ArenaState.STARTING)) {
            sendMessage("&e{player} &7left the game! &8({currentplayers}/{maxplayers}"
                    .replace("{player}", player.getName())
                    .replace("{currentplayers}", String.valueOf(players.size()))
                    .replace("{maxplayers}", String.valueOf(getTeams().size()*getMaxPlayersPerTeam())));
        } else {
            sendMessage("&c{player} &7has left!".replace("{player}", player.getDisplayName()));
        }
        if (arenaState.equals(ArenaState.STARTING) && getPlayers().size() < getMinPlayers()) {
            sendMessage(TextUtil.color("&cNot enough players! Countdown stopped!"));
            startingTask.cancelTask();
            setArenaState(ArenaState.WAITING);
        }
        if (!arenaState.equals(ArenaState.WAITING) && !arenaState.equals(ArenaState.STARTING) && !arenaState.equals(ArenaState.RESTARTING) && getTeams().stream().filter(team -> team.getMembers().size() == 0).count() > getTeams().size() - 2) {
            TextUtil.info("Not enough players in game " + id + ". Restarting...");
            setArenaState(ArenaState.RESTARTING);
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
            ArenaUtil.showLobbyPlayers(player);
            player.teleport(TextUtil.deserializeLocation(WoolWars.getInstance().getFileManager().getConfig().getString("lobby-location")));
        }
        players.clear();
    }

    public void setGamePhase(GamePhase gamePhase) {
        this.gamePhase = gamePhase;
        switch (gamePhase) {
            case PRE_ROUND:
                preRoundTask = new ArenaPreRoundTask(this);
                break;
            case ACTIVE_ROUND:
                playingTask = new ArenaPlayingTask(this);
                break;
            case ROUND_OVER:
                roundOverTask = new ArenaRoundOverTask(this);
                break;
            case GAME_ENDED:
                gameEndedTask = new ArenaGameEndedTask(this);
                break;
        }
    }

    public void setArenaState(ArenaState arenaState) {
        this.arenaState = arenaState;
        switch (arenaState) {
            case STARTING:
                startingTask = new ArenaStartingTask(this);
                break;
            case RESTARTING:
                restart();
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
        setGamePhase(GamePhase.NONE);
        setArenaState(ArenaState.WAITING);
    }

    public void assignTeams() {
        for (Player player : getPlayers()) {
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
        ArenaUtil.showDeadPlayers(this);
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

    public void sendMessage(String msg) {
        getPlayers().forEach(player -> player.sendMessage(TextUtil.color(msg)));
    }

    public void sendTitle(int stay, String title, String subtitle) {
        getPlayers().forEach(player -> Titles.sendTitle(player, 0, stay, 0, TextUtil.color(title), TextUtil.color(subtitle)));
    }

    public void playSound(String sound) {
        getPlayers().forEach(player -> XSound.play(player, sound));
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

}
