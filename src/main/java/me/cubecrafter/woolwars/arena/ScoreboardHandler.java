package me.cubecrafter.woolwars.arena;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.GameScoreboard;
import me.cubecrafter.woolwars.utils.GameUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class ScoreboardHandler implements Listener, Runnable {

    private final YamlConfiguration messages = WoolWars.getInstance().getFileManager().getMessages();
    private final List<String> lobbyLines = TextUtil.color(messages.getStringList("scoreboard.lobby-board"));
    private final List<String> waitingLines =  TextUtil.color(messages.getStringList("scoreboard.waiting-board"));
    private final List<String> startingLines = TextUtil.color(messages.getStringList("scoreboard.starting-board"));
    private final List<String> preRoundLines = TextUtil.color(messages.getStringList("scoreboard.preround-board"));
    private final List<String> playingLines = TextUtil.color(messages.getStringList("scoreboard.ingame-board"));
    private final String title = TextUtil.color(messages.getString("scoreboard.title"));

    public ScoreboardHandler() {
        Bukkit.getServer().getPluginManager().registerEvents(this, WoolWars.getInstance());
        Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), this, 0L, 10L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        createScoreboard(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (GameScoreboard.hasScoreboard(player)) {
            GameScoreboard.removeScoreboard(player);
        }
    }

    public void createScoreboard(Player player) {
        GameScoreboard scoreboard = GameScoreboard.createScoreboard(player);
        scoreboard.setTitle(title);
        updateScoreboard(player);
    }

    public void updateScoreboard(Player player) {
        GameScoreboard scoreboard;
        if (GameScoreboard.hasScoreboard(player)) {
            scoreboard = GameScoreboard.getScoreboard(player);
        } else {
            scoreboard = GameScoreboard.createScoreboard(player);
        }
        if (GameUtil.isPlaying(player) || GameUtil.isSpectating(player)) {
            Arena arena = GameUtil.getArenaByPlayer(player);
            switch (arena.getGameState()) {
                case WAITING:
                    scoreboard.setLines(TextUtil.parsePlaceholders(waitingLines, arena));
                    break;
                case PRE_ROUND:
                    scoreboard.setLines(TextUtil.parsePlaceholders(preRoundLines, arena));
                    break;
                case STARTING:
                    scoreboard.setLines(TextUtil.parsePlaceholders(startingLines, arena));
                    break;
                case ROUND_OVER:
                case RESTARTING:
                case PLAYING:
                    scoreboard.setLines(TextUtil.parsePlaceholders(playingLines, arena));
                    break;
            }
        } else {
            scoreboard.setLines(TextUtil.parsePlaceholders(lobbyLines));
        }
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateScoreboard(player);
        }
    }

}

