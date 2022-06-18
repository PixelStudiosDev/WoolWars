package me.cubecrafter.woolwars.listeners;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.GameArena;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.team.GameTeam;
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
        if (Configuration.SCOREBOARD_ENABLED.getAsBoolean()) {
            Bukkit.getServer().getPluginManager().registerEvents(this, WoolWars.getInstance());
            updateTask = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), this, 0L, Configuration.SCOREBOARD_REFRESH_INTERVAL.getAsInt());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        updateScoreboard(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        PlayerScoreboard.removeScoreboard(player);
    }

    public void updateScoreboard(Player player) {
        PlayerScoreboard scoreboard;
        if (PlayerScoreboard.hasScoreboard(player)) {
            scoreboard = PlayerScoreboard.getScoreboard(player);
        } else {
            scoreboard = PlayerScoreboard.createScoreboard(player);
            scoreboard.setTitle(TextUtil.color(Messages.SCOREBOARD_TITLE.getAsString()));
        }
        GameArena arena = ArenaUtil.getArenaByPlayer(player);
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
        updateTask.cancel();
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerScoreboard.removeScoreboard(player);
        }
    }

    private List<String> formatGameScoreboard(List<String> original, GameArena arena) {
        List<String> formatted = new ArrayList<>();
        for (String line : original) {
            if (!line.contains("{teams}")) {
                formatted.add(line);
                continue;
            }
            String teamFormat = Messages.SCOREBOARD_TEAM_FORMAT.getAsString();
            for (GameTeam team : arena.getTeams()) {
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

