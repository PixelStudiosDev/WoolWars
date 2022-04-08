package me.cubecrafter.woolwars.core;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.GameUtil;
import me.cubecrafter.woolwars.utils.SimpleScoreboard;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreboardHandler implements Listener, Runnable {

    private final YamlConfiguration messages = WoolWars.getInstance().getFileManager().getMessages();
    private final List<String> lobbyLines = TextUtil.color(messages.getStringList("scoreboard.lobby-board"));
    private final List<String> waitingLines =  TextUtil.color(messages.getStringList("scoreboard.waiting-board"));
    private final List<String> startingLines = TextUtil.color(messages.getStringList("scoreboard.starting-board"));
    private final List<String> playingLines = TextUtil.color(messages.getStringList("scoreboard.ingame-board"));
    private final String title = TextUtil.color(messages.getString("scoreboard.title"));

    private final HashMap<Player, SimpleScoreboard> scoreboards = new HashMap<>();

    public ScoreboardHandler() {
        Bukkit.getServer().getPluginManager().registerEvents(this, WoolWars.getInstance());
        Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), this, 0L, 20L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        SimpleScoreboard scoreboard = new SimpleScoreboard(WoolWars.getInstance());
        scoreboard.setTitle(title);
        scoreboard.addRows(lobbyLines);
        scoreboard.show(e.getPlayer());
        scoreboards.put(e.getPlayer(), scoreboard);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        scoreboards.remove(e.getPlayer());
    }

    @Override
    public void run() {
        for (Map.Entry<Player, SimpleScoreboard> entry : scoreboards.entrySet()) {
            Player player = entry.getKey();
            SimpleScoreboard scoreboard = entry.getValue();
            scoreboard.clearRows();
            if (GameUtil.isPlaying(player)) {
                Arena arena = GameUtil.getArenaByPlayer(player);
                switch (arena.getGameState()) {
                    case WAITING:
                        scoreboard.addRows(TextUtil.parsePlaceholders(waitingLines, arena));
                        break;
                    case PRE_ROUND:
                    case STARTING:
                        scoreboard.addRows(TextUtil.parsePlaceholders(startingLines, arena));
                        break;
                    case RESTARTING:
                    case PLAYING:
                        scoreboard.addRows(TextUtil.parsePlaceholders(playingLines, arena));
                        break;
                }
            } else {
                scoreboard.addRows(lobbyLines);
            }
        }
    }

}
