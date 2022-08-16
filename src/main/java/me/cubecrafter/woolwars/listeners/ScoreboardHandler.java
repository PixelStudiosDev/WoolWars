package me.cubecrafter.woolwars.listeners;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.api.events.player.PlayerJoinArenaEvent;
import me.cubecrafter.woolwars.api.events.player.PlayerLeaveArenaEvent;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.team.Team;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.PlayerScoreboard;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardHandler implements Listener, Runnable {

    private BukkitTask updateTask;

    public ScoreboardHandler() {
        Bukkit.getServer().getPluginManager().registerEvents(this, WoolWars.getInstance());
        if (Configuration.SCOREBOARD_GAME_ENABLED.getAsBoolean()) {
            updateTask = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), this, 0L, Configuration.SCOREBOARD_REFRESH_INTERVAL.getAsInt());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        updateScoreboard(player);
        PlayerScoreboard scoreboard = PlayerScoreboard.getOrCreate(player);
        if (Configuration.SCOREBOARD_LOBBY_ENABLED.getAsBoolean()) {
            scoreboard.show();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        PlayerScoreboard.removeScoreboard(player);
    }

    @EventHandler
    public void onArenaJoin(PlayerJoinArenaEvent e) {
        PlayerScoreboard scoreboard = PlayerScoreboard.getOrCreate(e.getPlayer());
        if (Configuration.SCOREBOARD_GAME_ENABLED.getAsBoolean()) {
            scoreboard.show();
        } else {
            scoreboard.hide();
        }
    }

    @EventHandler
    public void onArenaLeave(PlayerLeaveArenaEvent e) {
        PlayerScoreboard scoreboard = PlayerScoreboard.getOrCreate(e.getPlayer());
        if (Configuration.SCOREBOARD_LOBBY_ENABLED.getAsBoolean()) {
            scoreboard.show();
        } else {
            scoreboard.hide();
        }
    }

    public void updateScoreboard(Player player) {
        PlayerScoreboard scoreboard = PlayerScoreboard.getOrCreate(player);
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        scoreboard.setTitle(TextUtil.color(Messages.SCOREBOARD_TITLE.getAsString()));
        if (arena != null) {
            switch (arena.getGameState()) {
                case WAITING:
                    scoreboard.setLines(TextUtil.format(Messages.SCOREBOARD_WAITING.getAsStringList(), arena, player));
                    break;
                case STARTING:
                    scoreboard.setLines(TextUtil.format(Messages.SCOREBOARD_STARTING.getAsStringList(), arena, player));
                    break;
                default:
                    scoreboard.setLines(TextUtil.format(formatGameScoreboard(Messages.SCOREBOARD_PLAYING.getAsStringList(), arena), arena, player));
                    break;
            }
        } else {
            scoreboard.setLines(TextUtil.format(Messages.SCOREBOARD_LOBBY.getAsStringList(), player));
        }
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateScoreboard(player);
        }
    }

    public void disable() {
        if (updateTask != null) {
            updateTask.cancel();
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerScoreboard.removeScoreboard(player);
        }
    }

    private List<String> formatGameScoreboard(List<String> original, Arena arena) {
        List<String> formatted = new ArrayList<>();
        for (String line : original) {
            if (!line.contains("{teams}")) {
                formatted.add(line);
                continue;
            }
            String teamFormat = Messages.SCOREBOARD_TEAM_FORMAT.getAsString();
            for (Team team : arena.getTeams()) {
                StringBuilder builder = new StringBuilder();
                for (int index = 0; index < arena.getWinPoints(); index++) {
                    if (team.getPoints() <= index) {
                        builder.append(TextUtil.color("&7⬤"));
                    } else {
                        builder.append(TextUtil.color(team.getTeamColor().getChatColor() + "⬤"));
                    }
                }
                formatted.add(teamFormat.replace("{team_color}", team.getTeamColor().getChatColor().toString())
                        .replace("{team_letter}", team.getTeamLetter())
                        .replace("{team_progress}", builder.toString())
                        .replace("{team_points}", String.valueOf(team.getPoints()))
                        .replace("{win_points}", String.valueOf(arena.getWinPoints()))
                        .replace("{team_alive}", String.valueOf(team.getMembers().stream().filter(arena::isAlive).count())));
            }
        }
        return formatted;
    }

}

