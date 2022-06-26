package me.cubecrafter.woolwars.tasks;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.api.arena.GameState;
import me.cubecrafter.woolwars.api.database.PlayerData;
import me.cubecrafter.woolwars.api.events.arena.GameEndEvent;
import me.cubecrafter.woolwars.api.events.arena.RoundEndEvent;
import me.cubecrafter.woolwars.api.powerup.PowerUp;
import me.cubecrafter.woolwars.api.team.Team;
import me.cubecrafter.woolwars.arena.GameArena;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class RoundTask extends ArenaTask {

    private BukkitTask rotatePowerUpsTask;
    private final Map<Team, Integer> placedWool = new HashMap<>();
    private final Map<Player, Integer> roundKills = new HashMap<>();
    private final Map<Player, Integer> roundPlacedWool = new HashMap<>();
    private final Map<Player, Integer> roundBrokenBlocks = new HashMap<>();

    public RoundTask(GameArena arena) {
        super(arena);
    }

    @Override
    public void execute() {
        if ((arena.getTimer() <= 30 && arena.getTimer() % 10 == 0) || arena.getTimer() <= 5) {
            TextUtil.sendMessage(arena.getPlayers(),  Messages.TIME_LEFT_COUNTDOWN.getAsString().replace("{seconds}", String.valueOf(arena.getTimer())));
        }
    }

    @Override
    public void onEnd() {
        Map.Entry<Team, Integer> bestTeam = placedWool.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null);
        if (placedWool.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), bestTeam.getValue())).count() > 1) {
            sendRoundEndMessages(null, true, false);
            arena.setGameState(GameState.ROUND_OVER);
            RoundEndEvent event = new RoundEndEvent(arena, true, null, Collections.emptyList());
            Bukkit.getPluginManager().callEvent(event);
        } else {
            Team winner = bestTeam.getKey();
            winner.addPoint();
            if (winner.getPoints() == arena.getWinPoints()) {
                addWinsLossesStats(winner);
                sendRoundEndMessages(winner, false, true);
                arena.setGameState(GameState.GAME_ENDED);
                GameEndEvent event = new GameEndEvent(arena, winner, arena.getTeams().stream().filter(t -> !t.equals(winner)).collect(Collectors.toList()));
                Bukkit.getPluginManager().callEvent(event);
            } else {
                sendRoundEndMessages(winner, false, false);
                arena.setGameState(GameState.ROUND_OVER);
                RoundEndEvent event = new RoundEndEvent(arena, false, winner, arena.getTeams().stream().filter(t -> !t.equals(winner)).collect(Collectors.toList()));
                Bukkit.getPluginManager().callEvent(event);
            }
        }
        rotatePowerUpsTask.cancel();
        addRoundStats();
    }

    private void addRoundStats() {
        roundKills.forEach(arena::addKills);
        roundBrokenBlocks.forEach(arena::addBlocksBroken);
        roundPlacedWool.forEach(arena::addWoolPlaced);
    }

    @Override
    public int getDuration() {
        return Configuration.ACTIVE_ROUND_DURATION.getAsInt();
    }

    @Override
    public void onStart() {
        rotatePowerUpsTask = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), () -> arena.getPowerUps().forEach(PowerUp::rotate), 0L, 1L);
        for (Team team : arena.getTeams()) {
            placedWool.put(team, 0);
        }
    }

    public void addPlacedWool(Team team) {
        if (team == null) return;
        placedWool.merge(team, 1, Integer::sum);
    }

    public void removePlacedWool(Team team) {
        if (placedWool.get(team) == null) return;
        placedWool.put(team, placedWool.get(team) - 1);
        if (placedWool.get(team) == 0) placedWool.remove(team);
    }

    public void checkWinners() {
        for (Map.Entry<Team, Integer> entry : placedWool.entrySet()) {
            if (entry.getValue() < arena.getCenter().getTotalBlocks()) continue;
            Team team = entry.getKey();
            team.addPoint();
            if (team.getPoints() == arena.getWinPoints()) {
                addWinsLossesStats(team);
                sendRoundEndMessages(team, false, true);
                arena.setGameState(GameState.GAME_ENDED);
                GameEndEvent event = new GameEndEvent(arena, team, arena.getTeams().stream().filter(t -> !t.equals(team)).collect(Collectors.toList()));
                Bukkit.getPluginManager().callEvent(event);
                return;
            } else {
                sendRoundEndMessages(team, false, false);
                arena.setGameState(GameState.ROUND_OVER);
                RoundEndEvent event = new RoundEndEvent(arena, false, team, arena.getTeams().stream().filter(t -> !t.equals(team)).collect(Collectors.toList()));
                Bukkit.getPluginManager().callEvent(event);
            }
            addRoundStats();
            rotatePowerUpsTask.cancel();
            cancel();
        }
    }

    private void sendRoundEndMessages(Team winner, boolean draw, boolean lastRound) {
        if (lastRound) {
            String statsFormat = Messages.END_GAME_STATS_FORMAT.getAsString();
            String topKillsFormat;
            Map.Entry<Player, Integer> killsEntry = roundKills.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null);
            if (killsEntry == null) topKillsFormat = Messages.NONE_FORMAT.getAsString(); else {
                topKillsFormat = statsFormat.replace("{player}", killsEntry.getKey().getName()).replace("{value}", String.valueOf(killsEntry.getValue()));
            }
            String topPlacedWoolFormat;
            Map.Entry<Player, Integer> placedWoolEntry = roundPlacedWool.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null);
            if (placedWoolEntry == null) topPlacedWoolFormat = Messages.NONE_FORMAT.getAsString(); else {
                topPlacedWoolFormat = statsFormat.replace("{player}", placedWoolEntry.getKey().getName()).replace("{value}", String.valueOf(placedWoolEntry.getValue()));
            }
            String topBrokenBlocksFormat;
            Map.Entry<Player, Integer> brokenBlocksEntry = roundBrokenBlocks.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null);
            if (brokenBlocksEntry == null) topBrokenBlocksFormat = Messages.NONE_FORMAT.getAsString(); else {
                topBrokenBlocksFormat = statsFormat.replace("{player}", brokenBlocksEntry.getKey().getName()).replace("{value}", String.valueOf(brokenBlocksEntry.getValue()));
            }
            for (Team team : arena.getTeams()) {
                List<String> messages = Messages.END_GAME_MESSAGE.getAsStringList();
                messages.replaceAll(s -> s.replace("{team_status}", team.equals(winner) ? Messages.TEAM_WON_FORMAT.getAsString() : Messages.TEAM_LOST_FORMAT.getAsString())
                        .replace("{top_kills}", topKillsFormat)
                        .replace("{top_blocks_broken}", topBrokenBlocksFormat)
                        .replace("{top_wool_placed}", topPlacedWoolFormat));
                for (String message : messages) {
                    TextUtil.sendMessage(team.getMembers(), message);
                }
            }
        } else {
            for (Player player : arena.getPlayers()) {
                List<String> formatted = new ArrayList<>();
                for (String line : Messages.STATS_MESSAGE.getAsStringList()) {
                    if (!line.contains("{stats}")) {
                        formatted.add(line.replace("{round}", String.valueOf(arena.getRound())));
                        continue;
                    }
                    if (roundBrokenBlocks.get(player) == null && roundPlacedWool.get(player) == null && roundKills.get(player) == null) {
                        formatted.add(Messages.NO_STATS_ACHIEVED.getAsString());
                        continue;
                    }
                    if (roundKills.get(player) != null) {
                        formatted.add(Messages.STATS_KILLS.getAsString().replace("{kills}", String.valueOf(roundKills.get(player))));
                    }
                    if (roundPlacedWool.get(player) != null) {
                        formatted.add(Messages.STATS_PLACED_WOOL.getAsString().replace("{wool_placed}", String.valueOf(roundPlacedWool.get(player))));
                    }
                    if (roundBrokenBlocks.get(player) != null) {
                        formatted.add(Messages.STATS_BROKEN_BLOCKS.getAsString().replace("{blocks_broken}", String.valueOf(roundBrokenBlocks.get(player))));
                    }
                }
                TextUtil.sendMessage(player, formatted);
            }
        }
        if (draw) {
            TextUtil.sendTitle(arena.getPlayers(), 3, Messages.ROUND_DRAW_TITLE.getAsString(), Messages.ROUND_DRAW_SUBTITLE.getAsString());
            ArenaUtil.playSound(arena.getPlayers(), Configuration.SOUNDS_ROUND_DRAW.getAsString());
            return;
        }
        for (Team team : arena.getTeams()) {
            if (lastRound) {
                if (team.equals(winner)) {
                    TextUtil.sendTitle(team.getMembers(), 3,  Messages.WINNER_TITLE.getAsString(),  Messages.WINNER_SUBTITLE.getAsString());
                    ArenaUtil.playSound(team.getMembers(), Configuration.SOUNDS_GAME_WON.getAsString());
                } else {
                    TextUtil.sendTitle(team.getMembers(), 3,  Messages.LOSER_TITLE.getAsString(),  Messages.LOSER_SUBTITLE.getAsString());
                    ArenaUtil.playSound(team.getMembers(), Configuration.SOUNDS_GAME_LOST.getAsString());
                }
            } else {
                if (team.equals(winner)) {
                    TextUtil.sendTitle(team.getMembers(), 3, Messages.ROUND_WIN_TITLE.getAsString().replace("{points}", arena.getPointsFormatted()),  Messages.ROUND_WIN_SUBTITLE.getAsString());
                    ArenaUtil.playSound(team.getMembers(), Configuration.SOUNDS_ROUND_WON.getAsString());
                } else {
                    TextUtil.sendTitle(team.getMembers(), 3, Messages.ROUND_LOSE_TITLE.getAsString().replace("{points}", arena.getPointsFormatted()),  Messages.ROUND_LOSE_SUBTITLE.getAsString());
                    ArenaUtil.playSound(team.getMembers(), Configuration.SOUNDS_ROUND_LOST.getAsString());
                }
            }
        }
    }

    private void addWinsLossesStats(Team winner) {
        for (Team team : arena.getTeams()) {
            if (team.equals(winner)) {
                for (Player player : team.getMembers()) {
                    PlayerData data = ArenaUtil.getPlayerData(player);
                    data.setWins(data.getWins() + 1);
                }
            } else {
                for (Player player : team.getMembers()) {
                    PlayerData data = ArenaUtil.getPlayerData(player);
                    data.setLosses(data.getLosses() + 1);
                }
            }
        }
    }

    public void addKill(Player player) {
        roundKills.merge(player, 1, Integer::sum);
        PlayerData data = ArenaUtil.getPlayerData(player);
        data.setKills(data.getKills() + 1);
    }

    public void addPlacedWool(Player player) {
        roundPlacedWool.merge(player, 1, Integer::sum);
        PlayerData data = ArenaUtil.getPlayerData(player);
        data.setWoolPlaced(data.getWoolPlaced() + 1);
    }

    public void addBrokenBlock(Player player) {
        roundBrokenBlocks.merge(player, 1, Integer::sum);
        PlayerData data = ArenaUtil.getPlayerData(player);
        data.setBlocksBroken(data.getBlocksBroken() + 1);
    }

}
