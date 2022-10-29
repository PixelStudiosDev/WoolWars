/*
 * Wool Wars
 * Copyright (C) 2022 CubeCrafter Development
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.cubecrafter.woolwars.arena;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import lombok.Setter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.tasks.GameEndTask;
import me.cubecrafter.woolwars.arena.tasks.PreRoundTask;
import me.cubecrafter.woolwars.arena.tasks.RoundOverTask;
import me.cubecrafter.woolwars.arena.tasks.RoundTask;
import me.cubecrafter.woolwars.arena.tasks.StartingTask;
import me.cubecrafter.woolwars.api.events.arena.GameStateChangeEvent;
import me.cubecrafter.woolwars.api.events.player.PlayerJoinArenaEvent;
import me.cubecrafter.woolwars.api.events.player.PlayerLeaveArenaEvent;
import me.cubecrafter.woolwars.party.Party;
import me.cubecrafter.woolwars.party.provider.PartyProvider;
import me.cubecrafter.woolwars.team.TeamColor;
import me.cubecrafter.woolwars.arena.setup.SetupSession;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.powerup.PowerUp;
import me.cubecrafter.woolwars.team.Team;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.Cuboid;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import me.cubecrafter.woolwars.utils.VersionUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Getter
public class Arena {

    private final YamlConfiguration config;
    private final String id;
    private final String displayName;
    private final String group;
    private final Location lobby;
    private final int maxPlayersPerTeam;
    private final int minPlayers;
    private final int maxPlayers;
    private final int winPoints;
    private final List<Player> players = new ArrayList<>();
    private final List<Player> deadPlayers = new ArrayList<>();
    private final List<Block> placedBlocks = new ArrayList<>();
    private final List<Team> teams = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();
    private final Map<Player, Integer> kills = new HashMap<>();
    private final Map<Player, Integer> deaths = new HashMap<>();
    private final Map<Player, Integer> woolPlaced = new HashMap<>();
    private final Map<Player, Integer> blocksBroken = new HashMap<>();
    private final Cuboid center;
    private final Cuboid arenaRegion;
    private StartingTask startingTask;
    private RoundTask roundTask;
    private PreRoundTask preRoundTask;
    private RoundOverTask roundOverTask;
    private GameEndTask gameEndTask;
    private GameState gameState = GameState.WAITING;
    @Setter private int round = 0;
    @Setter private int timer = 0;
    @Setter private boolean centerLocked = false;

    public Arena(String id, YamlConfiguration arenaConfig) {
        this.id = id;
        this.config = arenaConfig;
        group = arenaConfig.getString("group");
        lobby = TextUtil.deserializeLocation(arenaConfig.getString("lobby-location"));
        displayName = TextUtil.color(arenaConfig.getString("displayname"));
        maxPlayersPerTeam = arenaConfig.getInt("max-players-per-team");
        minPlayers = arenaConfig.getInt("min-players");
        winPoints = arenaConfig.getInt("win-points");
        for (String key : arenaConfig.getConfigurationSection("teams").getKeys(false)) {
            TeamColor color = TeamColor.valueOf(key.toUpperCase());
            String name = arenaConfig.getString("teams." + key + ".name");
            Location spawn = TextUtil.deserializeLocation(arenaConfig.getString("teams." + key + ".spawn-location"));
            Location barrier1 = TextUtil.deserializeLocation(arenaConfig.getString("teams." + key + ".barrier.pos1"));
            Location barrier2 = TextUtil.deserializeLocation(arenaConfig.getString("teams." + key + ".barrier.pos2"));
            Location base1 = TextUtil.deserializeLocation(arenaConfig.getString("teams." + key + ".base.pos1"));
            Location base2 = TextUtil.deserializeLocation(arenaConfig.getString("teams." + key + ".base.pos2"));
            Team team = new Team(name, this, spawn, color, new Cuboid(barrier1, barrier2), new Cuboid(base1, base2));
            teams.add(team);
        }
        maxPlayers = teams.size() * maxPlayersPerTeam;
        Location point1 = TextUtil.deserializeLocation(arenaConfig.getString("center.pos1"));
        Location point2 = TextUtil.deserializeLocation(arenaConfig.getString("center.pos2"));
        center = new Cuboid(point1, point2);
        Location point3 = TextUtil.deserializeLocation(arenaConfig.getString("arena.pos1"));
        Location point4 = TextUtil.deserializeLocation(arenaConfig.getString("arena.pos2"));
        arenaRegion = new Cuboid(point3, point4);
        for (String line : arenaConfig.getStringList("powerups")) {
            Location location = TextUtil.deserializeLocation(line);
            PowerUp powerUp = new PowerUp(location, this);
            powerUps.add(powerUp);
        }
        killEntities();
        World world = arenaRegion.getWorld();
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("doFireTick", "false");
        world.setGameRuleValue("announceAdvancements", "false");
        world.setGameRuleValue("doWeatherCycle", "false");
        world.setFullTime(6000);
    }

    public void addPlayer(Player player, boolean partyCheck) {
        if (SetupSession.isActive(player)) {
            TextUtil.sendMessage(player, Messages.ALREADY_IN_SETUP_MODE.getAsString());
            return;
        }
        if (ArenaUtil.getArenaByPlayer(player) != null) {
            TextUtil.sendMessage(player, Messages.ALREADY_IN_ARENA.getAsString());
            return;
        }
        if (gameState != GameState.WAITING && gameState != GameState.STARTING) {
            TextUtil.sendMessage(player, Messages.GAME_ALREADY_STARTED.getAsString());
            return;
        }
        if (players.size() >= maxPlayers) {
            TextUtil.sendMessage(player, Messages.ARENA_FULL.getAsString());
            return;
        }
        PartyProvider partyProvider = WoolWars.getInstance().getPartyProvider();
        if (partyCheck && partyProvider.hasParty(player)) {
            Party party = partyProvider.getParty(player);
            if (!party.isLeader(player)) {
                TextUtil.sendMessage(player, Messages.PARTY_NOT_LEADER.getAsString());
                return;
            }
            if (party.getSize() > maxPlayers - players.size() || (maxPlayersPerTeam > 1 && party.getSize() > maxPlayersPerTeam)) {
                TextUtil.sendMessage(player, Messages.PARTY_TOO_BIG.getAsString());
                return;
            }
            if (party.getOnlineMembers().size() != party.getSize() - 1) {
                TextUtil.sendMessage(player, Messages.PARTY_OFFLINE_MEMBERS.getAsString());
                return;
            }
            if (party.getOnlineMembers().stream().anyMatch(member -> ArenaUtil.getArenaByPlayer(member) != null)) {
                TextUtil.sendMessage(player, Messages.PARTY_MEMBERS_IN_ARENA.getAsString());
                return;
            }
            for (Player member : party.getOnlineMembers()) {
                addPlayer(member, false);
            }
        }
        PlayerJoinArenaEvent event = new PlayerJoinArenaEvent(player, this);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        players.add(player);
        player.teleport(lobby);
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
                VersionUtil.showPlayer(player, online);
                VersionUtil.showPlayer(online, player);
            } else {
                VersionUtil.hidePlayer(player, online);
                VersionUtil.hidePlayer(online, player);
            }
        }
        ItemStack leaveItem = ItemBuilder.fromConfig(Config.LEAVE_ITEM.getAsSection()).setTag("leave-item").build();
        player.getInventory().setItem(Config.LEAVE_ITEM.getAsSection().getInt("slot"), leaveItem);
        TextUtil.sendMessage(players, Messages.PLAYER_JOIN_ARENA.getAsString()
                .replace("{player}", player.getName())
                .replace("{current}", String.valueOf(players.size()))
                .replace("{max}", String.valueOf(maxPlayers)));
        ArenaUtil.playSound(players, Config.SOUNDS_PLAYER_JOINED.getAsString());
        if (gameState == GameState.WAITING && getPlayers().size() >= getMinPlayers()) {
            setGameState(GameState.STARTING);
        }
    }

    public void forceStart() {
        if (gameState == GameState.WAITING) setGameState(GameState.STARTING);
    }

    public void removePlayer(Player player, boolean teleportToLobby) {
        PlayerLeaveArenaEvent event = new PlayerLeaveArenaEvent(player, this);
        Bukkit.getPluginManager().callEvent(event);
        PartyProvider partyProvider = WoolWars.getInstance().getPartyProvider();
        if (partyProvider.hasParty(player) && (gameState == GameState.STARTING || gameState == GameState.WAITING || gameState == GameState.GAME_ENDED)) {
            Party party = partyProvider.getParty(player);
            if (party.isLeader(player)) {
                for (Player member : party.getOnlineMembers()) {
                    if (!players.contains(member)) continue;
                    removePlayer(member, teleportToLobby);
                }
            }
        }
        players.remove(player);
        Team playerTeam = getTeamByPlayer(player);
        if (playerTeam != null) {
            playerTeam.removeMember(player);
        }
        player.getInventory().setArmorContents(null);
        player.getInventory().clear();
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setFireTicks(0);
        player.setFoodLevel(20);
        player.setHealth(20);
        player.setGameMode(GameMode.SURVIVAL);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        if (teleportToLobby) ArenaUtil.teleportToLobby(player);
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (ArenaUtil.isPlaying(online)) {
                VersionUtil.hidePlayer(player, online);
                VersionUtil.hidePlayer(online, player);
            } else {
                VersionUtil.showPlayer(player, online);
                VersionUtil.showPlayer(online, player);
            }
        }
        TextUtil.sendMessage(players, Messages.PLAYER_LEAVE_ARENA.getAsString()
                .replace("{player}", player.getDisplayName())
                .replace("{current}", String.valueOf(players.size()))
                .replace("{max}", String.valueOf(maxPlayers)));
        if (gameState == GameState.WAITING || gameState == GameState.STARTING) {
            ArenaUtil.playSound(players, Config.SOUNDS_PLAYER_LEFT.getAsString());
        }
        if (gameState == GameState.STARTING && getPlayers().size() < getMinPlayers()) {
            cancelTasks();
            TextUtil.sendMessage(players, Messages.START_CANCELLED.getAsString());
            setGameState(GameState.WAITING);
        }
        if (gameState != GameState.WAITING && gameState != GameState.STARTING && gameState != GameState.GAME_ENDED && teams.stream().filter(team -> team.getMembers().isEmpty()).count() > teams.size() - 2) {
            cancelTasks();
            setGameState(GameState.GAME_ENDED);
        }
    }

    public void removeAllPlayers() {
        for (Player player : players) {
            PlayerLeaveArenaEvent event = new PlayerLeaveArenaEvent(player, this);
            Bukkit.getPluginManager().callEvent(event);
            player.getInventory().setArmorContents(null);
            player.getInventory().clear();
            player.setFlying(false);
            player.setAllowFlight(false);
            player.setFireTicks(0);
            player.setFoodLevel(20);
            player.setHealth(20);
            player.setGameMode(GameMode.SURVIVAL);
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (ArenaUtil.isPlaying(online) && !players.contains(online)) {
                    VersionUtil.hidePlayer(player, online);
                    VersionUtil.hidePlayer(online, player);
                } else {
                    VersionUtil.showPlayer(player, online);
                    VersionUtil.showPlayer(online, player);
                }
            }
            ArenaUtil.teleportToLobby(player);
        }
        players.clear();
    }

    public void setGameState(GameState gameState) {
        GameStateChangeEvent event = new GameStateChangeEvent(this, this.gameState, gameState);
        Bukkit.getServer().getPluginManager().callEvent(event);
        this.gameState = gameState;
        switch (gameState) {
            case WAITING:
                cancelTasks();
                break;
            case STARTING:
                startingTask = new StartingTask(this);
                break;
            case PRE_ROUND:
                preRoundTask = new PreRoundTask(this);
                break;
            case ACTIVE_ROUND:
                roundTask = new RoundTask(this);
                break;
            case ROUND_OVER:
                roundOverTask = new RoundOverTask(this);
                break;
            case GAME_ENDED:
                gameEndTask = new GameEndTask(this);
                break;
        }
    }

    public void restart() {
        cancelTasks();
        removeAllPlayers();
        teams.forEach(Team::reset);
        removePlacedBlocks();
        fillCenter();
        powerUps.forEach(PowerUp::remove);
        setRound(0);
        setTimer(0);
        killEntities();
        deadPlayers.clear();
        kills.clear();
        deaths.clear();
        woolPlaced.clear();
        blocksBroken.clear();
        setGameState(GameState.WAITING);
    }

    public void assignTeams() {
        if (maxPlayersPerTeam > 1) {
            PartyProvider partyProvider = WoolWars.getInstance().getPartyProvider();
            List<Player> noParty = new ArrayList<>();
            List<Party> parties = new ArrayList<>();
            for (Player player : players) {
                if (partyProvider.hasParty(player)) {
                    Party party = partyProvider.getParty(player);
                    if (!parties.contains(party)) parties.add(party);
                } else {
                    noParty.add(player);
                }
            }
            parties.sort(Comparator.comparingInt(Party::getSize).reversed());
            for (Party party : parties) {
                for (Team team : teams) {
                    if (party.getSize() > maxPlayersPerTeam - team.getMembers().size()) continue;
                    team.addMember(party.getLeader());
                    for (Player player : party.getOnlineMembers()) {
                        if (!players.contains(player)) continue;
                        team.addMember(player);
                    }
                }
            }
            for (Player player : noParty) {
                Team minPlayers = teams.stream().min(Comparator.comparingInt(team -> team.getMembers().size())).orElse(teams.get(ThreadLocalRandom.current().nextInt(teams.size())));
                minPlayers.addMember(player);
            }
        } else {
            for (Player player : players) {
                Team minPlayers = teams.stream().min(Comparator.comparingInt(team -> team.getMembers().size())).orElse(teams.get(ThreadLocalRandom.current().nextInt(teams.size())));
                minPlayers.addMember(player);
            }
        }
    }

    public List<Player> getAlivePlayers() {
        return players.stream().filter(player -> !deadPlayers.contains(player)).collect(Collectors.toList());
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
        int seconds = timer % 60;
        return (minutes > 9 ? minutes : "0" + minutes) + ":" + (seconds > 9 ? seconds : "0" + seconds);
    }

    public void killEntities() {
        for (Entity entity : arenaRegion.getWorld().getEntities()) {
            EntityType type = entity.getType();
            if (type == EntityType.ITEM_FRAME || type == EntityType.ARMOR_STAND || type == EntityType.PAINTING || type == EntityType.PLAYER) continue;
            if (arenaRegion.isInside(entity.getLocation())) entity.remove();
        }
    }

    public void removePlacedBlocks() {
        placedBlocks.forEach(block -> block.setType(Material.AIR));
        placedBlocks.clear();
    }

    public void fillCenter() {
        String[] blocks = {"QUARTZ_BLOCK", "SNOW_BLOCK", "WHITE_WOOL"};
        center.getBlocks().forEach(block -> block.setType(XMaterial.matchXMaterial(blocks[ThreadLocalRandom.current().nextInt(blocks.length)]).get().parseMaterial()));
    }

    public String getPointsFormatted() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < teams.size(); i++) {
            Team team = teams.get(i);
            if (i > 0) {
                builder.append(" &f- ");
            }
            builder.append(team.getTeamColor().getChatColor()).append(team.getPoints());
        }
        return TextUtil.color(builder.toString());
    }

    public void addKills(Player player, int number) {
        kills.merge(player, number, Integer::sum);
    }

    public void addDeaths(Player player, int number) {
        deaths.merge(player, number, Integer::sum);
    }

    public void addWoolPlaced(Player player, int number) {
        woolPlaced.merge(player, number, Integer::sum);
    }

    public void addBlocksBroken(Player player, int number) {
        blocksBroken.merge(player, number, Integer::sum);
    }

    public void cancelTasks() {
        if (startingTask != null) startingTask.cancel();
        if (preRoundTask != null) preRoundTask.cancel();
        if (roundTask != null) roundTask.cancel();
        if (roundOverTask != null) roundOverTask.cancel();
        if (gameEndTask != null) gameEndTask.cancel();
    }

}
