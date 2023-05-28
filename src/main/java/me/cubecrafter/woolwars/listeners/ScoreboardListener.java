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

package me.cubecrafter.woolwars.listeners;

import fr.mrmicky.fastboard.FastBoard;
import me.cubecrafter.woolwars.api.events.player.PlayerJoinArenaEvent;
import me.cubecrafter.woolwars.api.events.player.PlayerLeaveArenaEvent;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.ArenaUtil;
import me.cubecrafter.woolwars.arena.team.Team;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.storage.player.PlayerManager;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.xutils.Events;
import me.cubecrafter.xutils.Tasks;
import me.cubecrafter.xutils.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScoreboardListener {

    private final Map<UUID, FastBoard> boards = new HashMap<>();
    private final BukkitTask task;

    public ScoreboardListener() {
        this.task = Tasks.repeat(() -> {
            boards.values().forEach(this::updateBoard);
        }, 0L, Config.SCOREBOARD_REFRESH_INTERVAL.asInt());

        // Register events
        Events.subscribe(PlayerJoinEvent.class, event -> {
            if (!Config.SCOREBOARD_LOBBY_ENABLED.asBoolean()) return;

            createBoard(event.getPlayer());
        });

        Events.subscribe(PlayerQuitEvent.class, event -> {
            deleteBoard(event.getPlayer());
        });

        Events.subscribe(PlayerJoinArenaEvent.class, event -> {
            Player player = event.getPlayer().getPlayer();
            if (!boards.containsKey(player.getUniqueId())) {
                createBoard(player);
            }
        });

        Events.subscribe(PlayerLeaveArenaEvent.class, event -> {
            if (event.getReason() == PlayerLeaveArenaEvent.Reason.DISCONNECT) {
                return;
            }
            if (!Config.SCOREBOARD_LOBBY_ENABLED.asBoolean()) {
                deleteBoard(event.getPlayer().getPlayer());
            }
        });
    }

    public void createBoard(Player player) {
        FastBoard board = new FastBoard(player);
        board.updateTitle(TextUtil.color(Messages.SCOREBOARD_TITLE.asString()));
        boards.put(player.getUniqueId(), board);
    }

    public void deleteBoard(Player player) {
        FastBoard board = boards.remove(player.getUniqueId());
        if (board != null) {
            board.delete();
        }
    }

    public void updateBoard(FastBoard board) {
        WoolPlayer player = PlayerManager.get(board.getPlayer());
        Arena arena = ArenaUtil.getArenaByPlayer(player);

        if (arena != null) {
            switch (arena.getState()) {
                case WAITING:
                    board.updateLines(TextUtil.color(ArenaUtil.parsePlaceholders(player, Messages.SCOREBOARD_WAITING.asStringList(), arena)));
                    break;
                case STARTING:
                    board.updateLines(TextUtil.color(ArenaUtil.parsePlaceholders(player, Messages.SCOREBOARD_STARTING.asStringList(), arena)));
                    break;
                default:
                    board.updateLines(TextUtil.color(ArenaUtil.parsePlaceholders(player, formatLines(Messages.SCOREBOARD_PLAYING.asStringList(), arena), arena)));
                    break;
            }
        } else {
            board.updateLines(TextUtil.color(ArenaUtil.parsePlaceholders(player, Messages.SCOREBOARD_LOBBY.asStringList())));
        }
    }

    private List<String> formatLines(List<String> lines, Arena arena) {
        List<String> formatted = new ArrayList<>();
        for (String line : lines) {
            if (!line.contains("{teams}")) {
                formatted.add(line);
                continue;
            }
            for (Team team : arena.getTeams()) {
                StringBuilder builder = new StringBuilder();
                for (int i = 1; i <= arena.getWinPoints(); i++) {
                    if (team.getPoints() < i) {
                        builder.append(Messages.SCOREBOARD_PROGRESS_EMPTY.asString());
                    } else {
                        builder.append(Messages.SCOREBOARD_PROGRESS_FULL.asString()
                                .replace("{team_color}", team.getTeamColor().getChatColor().toString()));
                    }
                }
                formatted.add(Messages.SCOREBOARD_TEAM_FORMAT.asString()
                        .replace("{team_color}", team.getTeamColor().getChatColor().toString())
                        .replace("{team_letter}", team.getLetter())
                        .replace("{team_progress}", builder.toString())
                        .replace("{team_points}", String.valueOf(team.getPoints()))
                        .replace("{win_points}", String.valueOf(arena.getWinPoints()))
                        .replace("{team_alive}", String.valueOf(team.getAliveCount())));
            }
        }
        return formatted;
    }

    public void disable() {
        task.cancel();
        boards.values().forEach(FastBoard::delete);
    }

}

