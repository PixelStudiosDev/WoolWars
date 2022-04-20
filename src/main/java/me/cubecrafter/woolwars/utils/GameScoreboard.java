package me.cubecrafter.woolwars.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GameScoreboard {

    private static final Map<UUID, GameScoreboard> scoreboards = new HashMap<>();

    public static boolean hasScoreboard(Player player) {
        return scoreboards.containsKey(player.getUniqueId());
    }

    public static GameScoreboard createScoreboard(Player player) {
        return new GameScoreboard(player);
    }

    public static GameScoreboard getScoreboard(Player player) {
        return scoreboards.get(player.getUniqueId());
    }

    public static void removeScoreboard(Player player) {
        scoreboards.remove(player.getUniqueId());
    }

    private final Scoreboard scoreboard;
    private final Objective sidebar;

    private GameScoreboard(Player player) {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        sidebar = scoreboard.registerNewObjective("woolwars", "dummy");
        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        for (int i = 1; i <= 15; i++) {
            Team team = scoreboard.registerNewTeam("line_" + i);
            team.addEntry(getEntry(i));
        }
        player.setScoreboard(scoreboard);
        scoreboards.put(player.getUniqueId(), this);
    }

    public void setTitle(String title) {
        String color = TextUtil.color(title);
        sidebar.setDisplayName(color.length() > 32 ? color.substring(0, 32) : color);
    }

    public void setLine(int line, String text) {
        Team team = scoreboard.getTeam("line_" + line);
        String entry = getEntry(line);
        if (!scoreboard.getEntries().contains(entry)) {
            sidebar.getScore(entry).setScore(line);
        }
        String color = TextUtil.color(text);
        String prefix = getFirstSplit(color);
        String suffix = getFirstSplit(ChatColor.getLastColors(prefix) + getSecondSplit(color));
        team.setPrefix(prefix);
        team.setSuffix(suffix);
    }

    public void removeLine(int line) {
        String entry = getEntry(line);
        if (scoreboard.getEntries().contains(entry)) {
            scoreboard.resetScores(entry);
        }
    }

    public void setLines(List<String> lines) {
        while (lines.size() > 15) {
            lines.remove(lines.size() - 1);
        }
        int slot = lines.size();
        if (slot < 15) {
            for (int i = (slot + 1); i <= 15; i++) {
                removeLine(i);
            }
        }
        for (String line : lines) {
            setLine(slot, line);
            slot--;
        }
    }

    private String getEntry(int slot) {
        return ChatColor.values()[slot].toString();
    }

    private String getFirstSplit(String text) {
        return text.length() > 16 ? text.substring(0, 16) : text;
    }

    private String getSecondSplit(String text) {
        if (text.length() > 32) {
            text = text.substring(0, 32);
        }
        return text.length() > 16 ? text.substring(16) : "";
    }

}
