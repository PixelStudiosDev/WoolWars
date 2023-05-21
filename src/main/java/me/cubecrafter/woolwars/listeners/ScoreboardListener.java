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

import me.cubecrafter.woolwars.api.events.arena.GameStateChangeEvent;
import me.cubecrafter.woolwars.api.events.player.PlayerJoinArenaEvent;
import me.cubecrafter.woolwars.api.events.player.PlayerLeaveArenaEvent;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.ArenaUtil;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.storage.player.PlayerManager;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.woolwars.arena.team.Team;
import me.cubecrafter.woolwars.utils.GameScoreboard;
import me.cubecrafter.xutils.Events;
import me.cubecrafter.xutils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardListener {

    private final BukkitTask task;

    public ScoreboardListener() {
        this.task = Tasks.repeat(() -> {
            Bukkit.getOnlinePlayers().forEach(player -> updateScoreboard(PlayerManager.get(player)));
        }, 0L, Config.SCOREBOARD_REFRESH_INTERVAL.asInt());

        // Register events
        Events.subscribe(PlayerJoinEvent.class, event -> {
            if (Config.SCOREBOARD_LOBBY_ENABLED.asBoolean()) {
                WoolPlayer player = PlayerManager.get(event.getPlayer());
                updateScoreboard(player).show();
            }
        });

        Events.subscribe(PlayerQuitEvent.class, event -> {
            GameScoreboard.remove(event.getPlayer());
        });

        Events.subscribe(PlayerJoinArenaEvent.class, event -> {
            updateScoreboard(event.getPlayer()).show();
        });

        Events.subscribe(PlayerLeaveArenaEvent.class, event -> {
            if (!Config.SCOREBOARD_LOBBY_ENABLED.asBoolean()) {
                GameScoreboard.getOrCreate(event.getPlayer().getPlayer()).hide();
            }
        });

        Events.subscribe(GameStateChangeEvent.class, event -> {
            for (WoolPlayer player : event.getArena().getPlayers()) {
                updateScoreboard(player);
            }
        });
    }

    public GameScoreboard updateScoreboard(WoolPlayer player) {
        GameScoreboard scoreboard = GameScoreboard.getOrCreate(player.getPlayer());
        scoreboard.setTitle(Messages.SCOREBOARD_TITLE.asString());

        Arena arena = ArenaUtil.getArenaByPlayer(player);

        if (arena != null) {
            switch (arena.getState()) {
                case WAITING:
                    scoreboard.setLines(ArenaUtil.parsePlaceholders(player, Messages.SCOREBOARD_WAITING.asStringList(), arena));
                    break;
                case STARTING:
                    scoreboard.setLines(ArenaUtil.parsePlaceholders(player, Messages.SCOREBOARD_STARTING.asStringList(), arena));
                    break;
                default:
                    scoreboard.setLines(ArenaUtil.parsePlaceholders(player, formatLines(Messages.SCOREBOARD_PLAYING.asStringList(), arena), arena));
                    break;
            }
        } else {
            scoreboard.setLines(ArenaUtil.parsePlaceholders(player, Messages.SCOREBOARD_LOBBY.asStringList()));
        }
        return scoreboard;
    }

    public void disable() {
        task.cancel();
        Bukkit.getOnlinePlayers().forEach(GameScoreboard::remove);
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

}

