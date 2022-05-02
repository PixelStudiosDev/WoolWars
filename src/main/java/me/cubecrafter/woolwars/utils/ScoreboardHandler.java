package me.cubecrafter.woolwars.utils;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.game.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class ScoreboardHandler implements Listener, Runnable {

    private final YamlConfiguration messages = WoolWars.getInstance().getFileManager().getMessages();
    private final List<String> lobbyLines = TextUtil.color(messages.getStringList("scoreboard.lobby-board"));
    private final List<String> waitingLines =  TextUtil.color(messages.getStringList("scoreboard.waiting-board"));
    private final List<String> startingLines = TextUtil.color(messages.getStringList("scoreboard.starting-board"));
    private final List<String> preRoundLines = TextUtil.color(messages.getStringList("scoreboard.preround-board"));
    private final List<String> playingLines = TextUtil.color(messages.getStringList("scoreboard.ingame-board"));
    private final String title = TextUtil.color(messages.getString("scoreboard.title"));
    private final BukkitTask updateTask;

    public ScoreboardHandler() {
        Bukkit.getServer().getPluginManager().registerEvents(this, WoolWars.getInstance());
        updateTask = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), this, 0L, 10L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        updateScoreboard(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        GameScoreboard.removeScoreboard(player);
    }

    public void updateScoreboard(Player player) {
        GameScoreboard scoreboard;
        if (GameScoreboard.hasScoreboard(player)) {
            scoreboard = GameScoreboard.getScoreboard(player);
        } else {
            scoreboard = GameScoreboard.createScoreboard(player);
            scoreboard.setTitle(title);
        }
        Arena arena = GameUtil.getArenaByPlayer(player);
        if (arena != null) {
            switch (arena.getGameState()) {
                case WAITING:
                    scoreboard.setLines(TextUtil.format(waitingLines, arena));
                    break;
                case PRE_ROUND:
                    scoreboard.setLines(TextUtil.format(preRoundLines, arena));
                    break;
                case STARTING:
                    scoreboard.setLines(TextUtil.format(startingLines, arena));
                    break;
                case ROUND_OVER:
                case RESTARTING:
                case GAME_ENDED:
                case PLAYING:
                    scoreboard.setLines(TextUtil.format(playingLines, arena));
                    break;
            }
        } else {
            scoreboard.setLines(TextUtil.format(lobbyLines));
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
            GameScoreboard.removeScoreboard(player);
        }
    }

}

