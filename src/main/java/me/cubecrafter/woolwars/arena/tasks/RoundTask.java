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

package me.cubecrafter.woolwars.arena.tasks;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.storage.PlayerData;
import me.cubecrafter.woolwars.api.events.arena.GameEndEvent;
import me.cubecrafter.woolwars.api.events.arena.RoundEndEvent;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.powerup.PowerUp;
import me.cubecrafter.woolwars.team.Team;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class RoundTask extends ArenaTask {

    private BukkitTask rotatePowerUpsTask;
    private final Map<Team, List<Block>> placedWool = new HashMap<>();
    private final Map<Player, Integer> roundKills = new HashMap<>();
    private final Map<Player, Integer> roundPlacedWool = new HashMap<>();
    private final Map<Player, Integer> roundBrokenBlocks = new HashMap<>();

    public RoundTask(Arena arena) {
        super(arena, Config.ACTIVE_ROUND_DURATION.getAsInt());
    }

    @Override
    public void execute() {
        if ((arena.getTimer() == 10 || arena.getTimer() <= 5)) {
            TextUtil.sendMessage(arena.getPlayers(),  Messages.TIME_LEFT_COUNTDOWN.getAsString().replace("{seconds}", String.valueOf(arena.getTimer())));
        }
    }

    @Override
    public void onEnd() {
        Map.Entry<Team, List<Block>> bestTeam = placedWool.entrySet().stream().max(Comparator.comparing(entry -> entry.getValue().size())).orElse(null);
        if (bestTeam == null || placedWool.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), bestTeam.getValue())).count() > 1) {
            sendRoundEndMessages(null, true, false);
            arena.setGameState(GameState.ROUND_OVER);
            RoundEndEvent event = new RoundEndEvent(arena, true, null, Collections.emptyList());
            Bukkit.getPluginManager().callEvent(event);
        } else {
            Team winner = bestTeam.getKey();
            winner.addPoint();
            if (winner.getPoints() == arena.getWinPoints()) {
                setWinner(winner);
            } else {
                sendRoundEndMessages(winner, false, false);
                arena.setGameState(GameState.ROUND_OVER);
                RoundEndEvent event = new RoundEndEvent(arena, false, winner, arena.getTeams().stream().filter(t -> !t.equals(winner)).collect(Collectors.toList()));
                Bukkit.getPluginManager().callEvent(event);
            }
        }
        addRoundStats();
    }

    private void addRoundStats() {
        roundKills.forEach(arena::addKills);
        roundBrokenBlocks.forEach(arena::addBlocksBroken);
        roundPlacedWool.forEach(arena::addWoolPlaced);
    }

    @Override
    public void onStart() {
        rotatePowerUpsTask = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), () -> arena.getPowerUps().forEach(PowerUp::rotate), 0L, 1L);
    }

    public void addPlacedWool(Team team, Block block) {
        if (!placedWool.containsKey(team)) {
            placedWool.put(team, new ArrayList<>());
        }
        placedWool.get(team).add(block);
    }

    public void removePlacedWool(Block block) {
        placedWool.values().forEach(blocks -> blocks.remove(block));
    }

    public void checkWinners() {
        for (Map.Entry<Team, List<Block>> entry : placedWool.entrySet()) {
            if (entry.getValue().size() < arena.getCenter().getTotalBlocks()) continue;
            Team team = entry.getKey();
            team.addPoint();
            if (team.getPoints() == arena.getWinPoints()) {
                setWinner(team);
            } else {
                sendRoundEndMessages(team, false, false);
                arena.setGameState(GameState.ROUND_OVER);
                RoundEndEvent event = new RoundEndEvent(arena, false, team, arena.getTeams().stream().filter(t -> !t.equals(team)).collect(Collectors.toList()));
                Bukkit.getPluginManager().callEvent(event);
            }
            addRoundStats();
            cancel();
        }
    }

    private void setWinner(Team team) {
        addGameStats(team);
        sendRoundEndMessages(team, false, true);
        arena.setGameState(GameState.GAME_ENDED);
        GameEndEvent event = new GameEndEvent(arena, team, arena.getTeams().stream().filter(t -> !t.equals(team)).collect(Collectors.toList()));
        Bukkit.getPluginManager().callEvent(event);
    }

    private void sendRoundEndMessages(Team winner, boolean draw, boolean lastRound) {
        if (lastRound) {
            for (Team team : arena.getTeams()) {
                List<String> messages = Messages.END_GAME_MESSAGE.getAsStringList();
                messages.replaceAll(s -> s.replace("{team_status}", team.equals(winner) ? Messages.TEAM_WON_FORMAT.getAsString() : Messages.TEAM_LOST_FORMAT.getAsString())
                        .replace("{top_kills}", formatStatistic(roundKills))
                        .replace("{top_blocks_broken}", formatStatistic(roundBrokenBlocks))
                        .replace("{top_wool_placed}", formatStatistic(roundPlacedWool)));
                messages.forEach(message -> TextUtil.sendMessage(team.getMembers(), message));
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
            TextUtil.sendTitle(arena.getPlayers(), 3, Messages.ROUND_DRAW_TITLE.getAsString().replace("{points}", arena.getPointsFormatted()), Messages.ROUND_DRAW_SUBTITLE.getAsString());
            ArenaUtil.playSound(arena.getPlayers(), Config.SOUNDS_ROUND_DRAW.getAsString());
            return;
        }
        for (Team team : arena.getTeams()) {
            if (lastRound) {
                if (team.equals(winner)) {
                    TextUtil.sendTitle(team.getMembers(), 3,  Messages.WINNER_TITLE.getAsString(),  Messages.WINNER_SUBTITLE.getAsString());
                    ArenaUtil.playSound(team.getMembers(), Config.SOUNDS_GAME_WON.getAsString());
                } else {
                    TextUtil.sendTitle(team.getMembers(), 3,  Messages.LOSER_TITLE.getAsString(),  Messages.LOSER_SUBTITLE.getAsString());
                    ArenaUtil.playSound(team.getMembers(), Config.SOUNDS_GAME_LOST.getAsString());
                }
            } else {
                if (team.equals(winner)) {
                    TextUtil.sendTitle(team.getMembers(), 3, Messages.ROUND_WIN_TITLE.getAsString().replace("{points}", arena.getPointsFormatted()),  Messages.ROUND_WIN_SUBTITLE.getAsString());
                    ArenaUtil.playSound(team.getMembers(), Config.SOUNDS_ROUND_WON.getAsString());
                } else {
                    TextUtil.sendTitle(team.getMembers(), 3, Messages.ROUND_LOSE_TITLE.getAsString().replace("{points}", arena.getPointsFormatted()),  Messages.ROUND_LOSE_SUBTITLE.getAsString());
                    ArenaUtil.playSound(team.getMembers(), Config.SOUNDS_ROUND_LOST.getAsString());
                }
            }
        }
    }

    private String formatStatistic(Map<Player, Integer> statistic) {
        String format = Messages.END_GAME_STATS_FORMAT.getAsString();
        Map.Entry<Player, Integer> entry = statistic.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null);
        if (entry == null) {
             return Messages.NONE_FORMAT.getAsString();
        } else {
            return format.replace("{player}", entry.getKey().getName()).replace("{value}", String.valueOf(entry.getValue()));
        }
    }

    private void addGameStats(Team winner) {
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

    @Override
    public void cancel() {
        super.cancel();
        rotatePowerUpsTask.cancel();
    }

}
