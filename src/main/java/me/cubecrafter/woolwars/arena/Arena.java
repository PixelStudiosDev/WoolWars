/*
 * Wool Wars
 * Copyright (C) 2023 CubeCrafter Development
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

import lombok.Getter;
import lombok.Setter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.api.events.arena.GameStateChangeEvent;
import me.cubecrafter.woolwars.api.events.player.PlayerJoinArenaEvent;
import me.cubecrafter.woolwars.api.events.player.PlayerLeaveArenaEvent;
import me.cubecrafter.woolwars.arena.setup.SetupSession;
import me.cubecrafter.woolwars.arena.tasks.ArenaTask;
import me.cubecrafter.woolwars.arena.tasks.GameEndTask;
import me.cubecrafter.woolwars.arena.tasks.PreRoundTask;
import me.cubecrafter.woolwars.arena.tasks.RoundOverTask;
import me.cubecrafter.woolwars.arena.tasks.RoundTask;
import me.cubecrafter.woolwars.arena.tasks.StartingTask;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.party.PartyProvider;
import me.cubecrafter.woolwars.powerup.PowerUp;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.woolwars.arena.team.Team;
import me.cubecrafter.woolwars.arena.team.TeamAssigner;
import me.cubecrafter.woolwars.utils.Cuboid;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.xutils.Events;
import me.cubecrafter.xutils.TextUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class Arena {

    private static final TeamAssigner assigner = new TeamAssigner();

    private final YamlConfiguration config;
    private final String id;
    private final String displayName;
    private final String group;
    private final Location lobby;

    private final int minPlayers;
    private final int maxPlayers;
    private final int maxPlayersPerTeam;
    private final int winPoints;

    private final World world;
    private final Cuboid centerRegion;
    private final Cuboid arenaRegion;

    private final List<WoolPlayer> players = new ArrayList<>();
    private final List<Team> teams = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();
    private final Set<Block> placedBlocks = new HashSet<>();

    private ArenaTask currentTask;
    private GameState state = GameState.WAITING;

    @Setter private int round;
    @Setter private int timer;
    @Setter private boolean centerLocked;

    public Arena(String id, YamlConfiguration config) {
        this.id = id;
        this.config = config;
        // Load config
        this.group = config.getString("group");
        this.lobby = TextUtil.parseLocation(config.getString("lobby-location"));
        this.displayName = TextUtil.color(config.getString("displayname"));
        this.maxPlayersPerTeam = config.getInt("max-players-per-team");
        this.minPlayers = config.getInt("min-players");
        this.winPoints = config.getInt("win-points");
        // Load regions
        this.centerRegion = new Cuboid(
                TextUtil.parseLocation(config.getString("center.pos1")),
                TextUtil.parseLocation(config.getString("center.pos2"))
        );
        this.arenaRegion = new Cuboid(
                TextUtil.parseLocation(config.getString("arena.pos1")),
                TextUtil.parseLocation(config.getString("arena.pos2"))
        );
        this.world = arenaRegion.getWorld();
        // Load teams
        for (String key : config.getConfigurationSection("teams").getKeys(false)) {
            teams.add(new Team(this, config.getConfigurationSection("teams." + key)));
        }
        this.maxPlayers = teams.size() * maxPlayersPerTeam;
        // Load powerups
        for (String line : config.getStringList("powerups")) {
            Location location = TextUtil.parseLocation(line);
            powerUps.add(new PowerUp(this, location));
        }
        // Adjust some game rules
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("doFireTick", "false");
        world.setGameRuleValue("announceAdvancements", "false");
        world.setGameRuleValue("doWeatherCycle", "false");
        world.setGameRuleValue("randomTickSpeed", "0");
        world.setFullTime(6000);

        killEntities();
    }

    public void addPlayer(WoolPlayer player, boolean partyCheck) {
        // Check if the player can join
        if (SetupSession.check(player)) {
            player.send(Messages.ALREADY_IN_SETUP_MODE.asString());
            return;
        }
        if (ArenaUtil.getArenaByPlayer(player) != null) {
            player.send(Messages.ALREADY_IN_ARENA.asString());
            return;
        }
        if (state != GameState.WAITING && state != GameState.STARTING) {
            player.send(Messages.GAME_ALREADY_STARTED.asString());
            return;
        }
        if (players.size() >= maxPlayers) {
            player.send(Messages.ARENA_FULL.asString());
            return;
        }
        // If the player is in a party, check if the party can join
        PartyProvider party = WoolWars.get().getPartyProvider();
        if (party != null && partyCheck && party.hasParty(player)) {
            if (!party.isLeader(player)) {
                player.send(Messages.PARTY_NOT_LEADER.asString());
                return;
            }
            if (party.getSize(player) > maxPlayers - players.size()) {
                player.send(Messages.PARTY_TOO_BIG.asString());
                return;
            }
            if (!party.isOnline(player)) {
                player.send(Messages.PARTY_OFFLINE_MEMBERS.asString());
                return;
            }
            if (party.getMembers(player).stream().anyMatch(ArenaUtil::isPlaying)) {
                player.send(Messages.PARTY_MEMBERS_IN_ARENA.asString());
                return;
            }
            for (WoolPlayer member : party.getMembers(player)) {
                addPlayer(member, false);
            }
        }
        if (Events.call(new PlayerJoinArenaEvent(player, this))) {
            return;
        }
        // The player can join
        players.add(player);
        player.reset(GameMode.ADVENTURE);
        player.teleport(lobby);
        // Give leave item
        ItemStack item = ItemBuilder.fromConfig(Config.LEAVE_ITEM.asSection()).setTag("leave").build();
        player.getPlayer().getInventory().setItem(Config.LEAVE_ITEM.asSection().getInt("slot"), item);
        // Hide the player from players in other arenas
        playSound(Config.SOUNDS_PLAYER_JOINED.asString());
        broadcast(Messages.PLAYER_JOIN_ARENA.asString()
                .replace("{player}", player.getPlayer().getName())
                .replace("{current}", String.valueOf(players.size()))
                .replace("{max}", String.valueOf(maxPlayers)));
        // Start the game if the minimum players are reached
        if (state == GameState.WAITING && players.size() >= minPlayers) {
            setState(GameState.STARTING);
        }
    }

    public void removePlayer(WoolPlayer player, PlayerLeaveArenaEvent.Reason reason) {
        Events.call(new PlayerLeaveArenaEvent(player, this, reason));
        players.remove(player);
        player.reset(GameMode.SURVIVAL);
        player.getData().resetArenaStats();
        player.getData().resetRoundStats();
        // If the game hasn't started yet, or has already ended, remove the whole party
        PartyProvider party = WoolWars.get().getPartyProvider();
        if (party != null && party.hasParty(player) && party.isLeader(player) && (state == GameState.WAITING || state == GameState.STARTING || state == GameState.GAME_ENDED)) {
            for (WoolPlayer member : party.getMembers(player)) {
                if (isPlaying(member)) {
                    removePlayer(member, reason);
                }
            }
        }
        // Remove the player from the current team
        Team playerTeam = getTeam(player);
        if (playerTeam != null) {
            playerTeam.removeMember(player);
        }
        if (reason != PlayerLeaveArenaEvent.Reason.PLAY_AGAIN) {
            player.teleportToLobby();
        }
        broadcast(Messages.PLAYER_LEAVE_ARENA.asString()
                .replace("{player}", player.getPlayer().getName())
                .replace("{current}", String.valueOf(players.size()))
                .replace("{max}", String.valueOf(maxPlayers)));
        if (state == GameState.WAITING || state == GameState.STARTING) {
            playSound(Config.SOUNDS_PLAYER_LEFT.asString());
        }
        // Check if the game should be cancelled
        if (state == GameState.STARTING && getPlayers().size() < getMinPlayers()) {
            broadcast(Messages.START_CANCELLED.asString());
            setState(GameState.WAITING);
        }
        // If there are less than 2 teams left, end the game
        if (state != GameState.WAITING && state != GameState.STARTING && state != GameState.GAME_ENDED && teams.stream().filter(team -> team.getSize() == 0).count() > teams.size() - 2) {
            setState(GameState.GAME_ENDED);
        }
    }

    public void forceStart() {
        if (state == GameState.WAITING) {
            setState(GameState.STARTING);
        }
    }

    public void assignTeams() {
        if (state == GameState.STARTING) {
            assigner.assign(this);
        }
    }

    public void setState(GameState state) {
        Events.call(new GameStateChangeEvent(this, this.state, state));
        this.state = state;
        if (currentTask != null) {
            currentTask.cancel();
        }
        switch (state) {
            case STARTING:
                currentTask = new StartingTask(this);
                break;
            case PRE_ROUND:
                currentTask = new PreRoundTask(this);
                break;
            case ACTIVE_ROUND:
                currentTask = new RoundTask(this);
                break;
            case ROUND_OVER:
                currentTask = new RoundOverTask(this);
                break;
            case GAME_ENDED:
                currentTask = new GameEndTask(this);
                break;
        }
    }

    public boolean isJoinable() {
        return (state == GameState.WAITING || state == GameState.STARTING) && players.size() < maxPlayers;
    }

    public void restart() {
        for (WoolPlayer player : new ArrayList<>(players)) {
            removePlayer(player, PlayerLeaveArenaEvent.Reason.GAME_END);
        }
        teams.forEach(Team::reset);
        powerUps.forEach(PowerUp::remove);
        clearBlocks();
        fillCenter();
        setRound(0);
        setTimer(0);
        killEntities();
        setState(GameState.WAITING);
    }

    public List<WoolPlayer> getAlivePlayers() {
        return players.stream().filter(WoolPlayer::isAlive).collect(Collectors.toList());
    }

    public List<WoolPlayer> getDeadPlayers() {
        return players.stream().filter(player -> !player.isAlive()).collect(Collectors.toList());
    }

    public Team getTeam(WoolPlayer player) {
        return teams.stream().filter(team -> team.getMembers().contains(player)).findAny().orElse(null);
    }

    public boolean isPlaying(WoolPlayer player) {
        return players.contains(player);
    }

    public boolean isTeammate(WoolPlayer player, WoolPlayer other) {
        return getTeam(player).isMember(other);
    }

    public String getTimerFormatted() {
        int minutes = (timer / 60) % 60;
        int seconds = timer % 60;
        return (minutes > 9 ? minutes : "0" + minutes) + ":" + (seconds > 9 ? seconds : "0" + seconds);
    }

    public void killEntities() {
        for (Entity entity : world.getEntities()) {
            if (!arenaRegion.isInside(entity.getLocation())) {
                continue;
            }
            EntityType type = entity.getType();
            if (type == EntityType.PLAYER || type == EntityType.ITEM_FRAME || type == EntityType.PAINTING) {
                continue;
            }
            entity.remove();
        }
    }

    public void clearBlocks() {
        placedBlocks.forEach(block -> block.setType(Material.AIR));
        placedBlocks.clear();
    }

    public void fillCenter() {
        centerRegion.fill(Config.CENTER_BLOCKS.asStringList());
    }

    public String getPointsFormatted() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < teams.size(); i++) {
            Team team = teams.get(i);
            if (i > 0) {
                builder.append(Messages.POINTS_TITLE_SEPARATOR.asString());
            }
            builder.append(team.getTeamColor().getChatColor()).append(team.getPoints());
        }
        return TextUtil.color(builder.toString());
    }

    public void broadcast(String message) {
        players.forEach(player -> player.send(message));
    }

    public void broadcast(List<String> messages) {
        messages.forEach(this::broadcast);
    }

    public void playSound(String sound) {
        players.forEach(player -> player.playSound(sound));
    }

    public void sendTitle(String title, String subtitle, int stay) {
        players.forEach(player -> player.sendTitle(title, subtitle, stay));
    }

    public void broadcastActionBar(String message) {
        players.forEach(player -> player.sendActionBar(message));
    }

}
