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

package me.cubecrafter.woolwars.arena.tasks;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.api.events.arena.GameEndEvent;
import me.cubecrafter.woolwars.api.events.arena.RoundEndEvent;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.powerup.PowerUp;
import me.cubecrafter.woolwars.storage.player.StatisticType;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.woolwars.arena.team.Team;
import me.cubecrafter.xutils.Events;
import me.cubecrafter.xutils.Tasks;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RoundTask extends ArenaTask {

    private final Map<Team, List<BlockVector>> wool = new HashMap<>();
    private BukkitTask rotationTask;

    public RoundTask(Arena arena) {
        super(arena, Config.ACTIVE_ROUND_DURATION.asInt());
    }

    @Override
    public void start() {
        rotationTask = Tasks.repeat(() -> arena.getPowerUps().forEach(PowerUp::rotate), 0L, 1L);

        if (Config.CENTER_UNLOCK_DELAY.asDouble() > 0) {
            arena.setCenterLocked(true);
            new BukkitRunnable() {
                double delay = Config.CENTER_UNLOCK_DELAY.asDouble();

                @Override
                public void run() {
                    if (arena.getState() != GameState.ACTIVE_ROUND) {
                        arena.setCenterLocked(false);
                        cancel();
                        return;
                    }
                    if (delay <= 0) {
                        arena.setCenterLocked(false);
                        arena.broadcastActionBar(Messages.CENTER_UNLOCKED.asString());
                        cancel();
                    } else {
                        arena.broadcastActionBar(Messages.CENTER_UNLOCK_COUNTDOWN.asString().replace("{seconds}", String.format("%.1f", delay)));
                        delay -= 0.1;
                    }
                }
            }.runTaskTimer(WoolWars.get(), 2L, 2L);
        }
    }

    @Override
    public void execute() {
        if ((arena.getTimer() == 10 || arena.getTimer() <= 5)) {
            arena.broadcast(Messages.TIME_LEFT_COUNTDOWN.asString().replace("{seconds}", String.valueOf(arena.getTimer())));
            arena.playSound(Config.SOUNDS_COUNTDOWN.asString());
        }
    }

    @Override
    public GameState end() {
        // Find the team with the most wool placed
        Map.Entry<Team, List<BlockVector>> best = wool.entrySet().stream().max(Comparator.comparingInt(entry -> entry.getValue().size())).orElse(null);
        // Check if there is a draw
        if (best == null || wool.entrySet().stream().filter(entry -> entry.getValue().size() == best.getValue().size()).count() > 1) {
            return stopRound(null);
        } else {
            return stopRound(best.getKey());
        }
    }

    public void checkWinner() {
        // Check if a team has won
        for (Map.Entry<Team, List<BlockVector>> entry : wool.entrySet()) {
            if (entry.getValue().size() < arena.getCenterRegion().getBlockCount()) continue;
            arena.setState(stopRound(entry.getKey()));
            break;
        }
    }

    public GameState stopRound(Team winner) {
        // Check if the game should end
        if (winner != null && winner.addPoint() == arena.getWinPoints()) {
            Events.call(new GameEndEvent(arena, winner, arena.getTeams().stream().filter(team -> !team.equals(winner)).collect(Collectors.toList())));

            winner.sendTitle(Messages.WINNER_TITLE.asString(), Messages.WINNER_SUBTITLE.asString(), 3);
            winner.playSound(Config.SOUNDS_GAME_WON.asString());

            for (Team team : arena.getTeams()) {
                List<String> messages = Messages.END_GAME_MESSAGE.asStringList();
                messages.replaceAll(message -> message.replace("{team_status}", team.equals(winner) ? Messages.TEAM_WON_FORMAT.asString() : Messages.TEAM_LOST_FORMAT.asString())
                        .replace("{top_kills}", formatStatistic(StatisticType.KILLS))
                        .replace("{top_blocks_broken}", formatStatistic(StatisticType.BLOCKS_BROKEN))
                        .replace("{top_wool_placed}", formatStatistic(StatisticType.WOOL_PLACED)));
                team.broadcast(messages);

                if (team.equals(winner)) continue;

                team.sendTitle(Messages.LOSER_TITLE.asString(), Messages.LOSER_SUBTITLE.asString(), 3);
                team.playSound(Config.SOUNDS_GAME_LOST.asString());
            }
            return GameState.GAME_ENDED;
        }
        // Send round statistics to each player
        for (WoolPlayer player : arena.getPlayers()) {
            List<String> formatted = new ArrayList<>();
            for (String line : Messages.STATS_MESSAGE.asStringList()) {
                if (!line.contains("{stats}")) {
                    formatted.add(line.replace("{round}", String.valueOf(arena.getRound())));
                    continue;
                }
                int damage = player.getData().getRoundStatistic(StatisticType.DAMAGE);
                int kills = player.getData().getRoundStatistic(StatisticType.KILLS);
                int woolPlaced = player.getData().getRoundStatistic(StatisticType.WOOL_PLACED);
                int blocksBroken = player.getData().getRoundStatistic(StatisticType.BLOCKS_BROKEN);
                // Check if the player has any stats
                if (blocksBroken == 0 && woolPlaced == 0 && kills == 0 && damage == 0) {
                    formatted.add(Messages.NO_STATS_ACHIEVED.asString());
                    continue;
                }
                if (damage != 0) {
                    formatted.add(Messages.STATS_DAMAGE.asString().replace("{damage}", String.valueOf(damage)));
                }
                if (kills != 0) {
                    formatted.add(Messages.STATS_KILLS.asString().replace("{kills}", String.valueOf(kills)));
                }
                if (woolPlaced != 0) {
                    formatted.add(Messages.STATS_PLACED_WOOL.asString().replace("{wool_placed}", String.valueOf(woolPlaced)));
                }
                if (blocksBroken != 0) {
                    formatted.add(Messages.STATS_BROKEN_BLOCKS.asString().replace("{blocks_broken}", String.valueOf(blocksBroken)));
                }
            }
            player.send(formatted);
        }
        // Check if it's a draw
        if (winner == null) {
            Events.call(new RoundEndEvent(arena, true, null, Collections.emptyList()));

            arena.sendTitle(Messages.ROUND_DRAW_TITLE.asString().replace("{points}", arena.getPointsFormatted()), Messages.ROUND_DRAW_SUBTITLE.asString(), 3);
            arena.playSound(Config.SOUNDS_ROUND_LOST.asString());
        } else {
            Events.call(new RoundEndEvent(arena, false, winner, arena.getTeams().stream().filter(team -> !team.equals(winner)).collect(Collectors.toList())));

            winner.sendTitle(Messages.ROUND_WIN_TITLE.asString().replace("{points}", arena.getPointsFormatted()), Messages.ROUND_WIN_SUBTITLE.asString(), 3);
            winner.playSound(Config.SOUNDS_ROUND_WON.asString());

            for (Team team : arena.getTeams()) {
                if (team.equals(winner)) continue;

                team.sendTitle(Messages.ROUND_LOSE_TITLE.asString().replace("{points}", arena.getPointsFormatted()), Messages.ROUND_LOSE_SUBTITLE.asString(), 3);
                team.playSound(Config.SOUNDS_ROUND_LOST.asString());
            }
        }
        return GameState.ROUND_OVER;
    }

    public void addPlacedWool(Team team, Block block) {
        wool.computeIfAbsent(team, key -> new ArrayList<>()).add(block.getLocation().toVector().toBlockVector());
        checkWinner();
    }

    public void removePlacedWool(Block block) {
        wool.values().forEach(blocks -> blocks.remove(block.getLocation().toVector().toBlockVector()));
    }

    public String formatStatistic(StatisticType type) {
        WoolPlayer highest = arena.getPlayers().stream().max(Comparator.comparingInt(player -> player.getData().getArenaStatistic(type))).orElse(null);
        if (highest == null) {
            return Messages.NONE_FORMAT.asString();
        } else {
            return Messages.END_GAME_STATS_FORMAT.asString().replace("{player}", highest.getName())
                    .replace("{value}", String.valueOf(highest.getData().getArenaStatistic(type)));
        }
    }

    @Override
    public void cancel() {
        super.cancel();
        rotationTask.cancel();
    }

}
