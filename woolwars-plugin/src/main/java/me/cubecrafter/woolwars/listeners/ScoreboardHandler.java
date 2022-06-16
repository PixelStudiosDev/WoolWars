package me.cubecrafter.woolwars.listeners;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.GameArena;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.PlayerScoreboard;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

public class ScoreboardHandler implements Listener, Runnable {

    private YamlConfiguration messages = WoolWars.getInstance().getFileManager().getMessages();
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
            scoreboard.setTitle(TextUtil.color(messages.getString("scoreboard.title")));
        }
        GameArena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena != null) {
            switch (arena.getGameState()) {
                case WAITING:
                    scoreboard.setLines(TextUtil.format(TextUtil.color(messages.getStringList("scoreboard.waiting")), arena, player));
                    break;
                case STARTING:
                    scoreboard.setLines(TextUtil.format(TextUtil.color(messages.getStringList("scoreboard.starting")), arena, player));
                    break;
                default:
                    scoreboard.setLines(TextUtil.format(TextUtil.color(messages.getStringList("scoreboard.playing")), arena, player));
                    break;
            }
        } else {
            scoreboard.setLines(TextUtil.format(TextUtil.color(messages.getStringList("scoreboard.lobby")), player));
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

    public void reload() {
        messages = WoolWars.getInstance().getFileManager().getMessages();
    }

}

