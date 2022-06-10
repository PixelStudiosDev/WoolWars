package me.cubecrafter.woolwars.utils;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.game.arena.Arena;
import me.cubecrafter.woolwars.game.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardHandler implements Listener, Runnable {

    private YamlConfiguration messages = WoolWars.getInstance().getFileManager().getMessages();
    private final BukkitTask updateTask;

    public ScoreboardHandler() {
        Bukkit.getServer().getPluginManager().registerEvents(this, WoolWars.getInstance());
        updateTask = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), this, 0L, 20L);
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
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena != null) {
            switch (arena.getArenaState()) {
                case WAITING:
                    scoreboard.setLines(TextUtil.format(TextUtil.color(messages.getStringList("scoreboard.waiting")), arena, player));
                    break;
                case STARTING:
                    scoreboard.setLines(TextUtil.format(TextUtil.color(messages.getStringList("scoreboard.starting")), arena, player));
                    break;
                case PLAYING:
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

