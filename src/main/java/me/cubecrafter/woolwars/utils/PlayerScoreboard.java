/*
 * Wool Wars
 * Copyright (C) 2022 CubeCrafter Development
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

package me.cubecrafter.woolwars.utils;

import com.cryptomorin.xseries.ReflectionUtils;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerScoreboard {

    private static final Map<UUID, PlayerScoreboard> scoreboards = new HashMap<>();

    public static PlayerScoreboard getOrCreate(Player player) {
        if (scoreboards.containsKey(player.getUniqueId())) {
            return scoreboards.get(player.getUniqueId());
        } else {
            return new PlayerScoreboard(player);
        }
    }

    public static void removeScoreboard(Player player) {
        scoreboards.remove(player.getUniqueId());
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    private final Player player;
    private final Scoreboard scoreboard;
    private Objective sidebar;

    private PlayerScoreboard(Player player) {
        this.player = player;
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        sidebar = scoreboard.registerNewObjective("woolwars", "dummy");
        for (int i = 1; i <= 15; i++) {
            org.bukkit.scoreboard.Team team = scoreboard.registerNewTeam("line_" + i);
            team.addEntry(generateEntry(i));
        }
        player.setScoreboard(scoreboard);
        scoreboards.put(player.getUniqueId(), this);
    }

    public void setTitle(String title) {
        String color = TextUtil.color(title);
        sidebar.setDisplayName(color.length() > 32 ? color.substring(0, 32) : color);
    }

    public void setGamePrefix(Team team) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerScoreboard sidebar = getOrCreate(player);
            sidebar.setGamePrefixInternal(this.player, team);
        }
    }

    private void setGamePrefixInternal(Player player, Team team) {
        org.bukkit.scoreboard.Team scoreboardTeam = scoreboard.getTeam(team.getArena().getId() + "_" + team.getName());
        if (scoreboardTeam == null) {
            scoreboardTeam = scoreboard.registerNewTeam(team.getArena().getId() + "_" + team.getName());
            scoreboardTeam.setCanSeeFriendlyInvisibles(false);
            if (ReflectionUtils.supports(12)) {
                scoreboardTeam.setColor(team.getTeamColor().getChatColor());
            }
        }
        String color = TextUtil.color(Configuration.NAME_TAGS_PREFIX.getAsString()
                .replace("{team_color}", team.getTeamColor().getChatColor().toString())
                .replace("{team}", team.getName())
                .replace("{team_letter}", team.getTeamLetter()));
        scoreboardTeam.setPrefix(getFirstSplit(color));
        scoreboardTeam.addEntry(player.getName());
    }

    public void removeGamePrefix(Team team) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerScoreboard sidebar = getOrCreate(player);
            if (sidebar == null) continue;
            sidebar.removeGamePrefixInternal(this.player, team);
        }
    }

    private void removeGamePrefixInternal(Player player, Team team) {
        org.bukkit.scoreboard.Team scoreboardTeam = scoreboard.getTeam(team.getArena().getId() + "_" + team.getName());
        if (scoreboardTeam == null) return;
        scoreboardTeam.removeEntry(player.getName());
        if (scoreboardTeam.getSize() == 0) {
            scoreboardTeam.unregister();
        }
    }
    
    public void setLine(int line, String text) {
        org.bukkit.scoreboard.Team team = scoreboard.getTeam("line_" + line);
        String entry = generateEntry(line);
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
        String entry = generateEntry(line);
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

    private String generateEntry(int slot) {
        return ChatColor.values()[slot].toString();
    }

    private String getFirstSplit(String text) {
        String split = text.length() > 16 ? text.substring(0, 16) : text;
        return split.endsWith("§") ? split.substring(0, split.length() - 1) : split;
    }

    private String getSecondSplit(String text) {
        if (text.length() > 32) {
            text = text.substring(0, 32);
        }
        boolean previousCode = text.length() > 16 && text.charAt(15) == '§';
        String split = text.length() > 16 ? text.substring(16) : "";
        return previousCode ? split.length() == 16 ? "§" + split.substring(0, split.length() - 1) : "§" + split : split;
    }

    public void hide() {
        scoreboard.clearSlot(DisplaySlot.SIDEBAR);
    }

    public void show() {
        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

}
