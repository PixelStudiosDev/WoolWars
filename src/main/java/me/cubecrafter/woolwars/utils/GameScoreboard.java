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

package me.cubecrafter.woolwars.utils;

import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.arena.team.Team;
import me.cubecrafter.xutils.ReflectionUtil;
import me.cubecrafter.xutils.TextUtil;
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
import java.util.function.BiFunction;

public class GameScoreboard {

    private static final Map<UUID, GameScoreboard> scoreboards = new HashMap<>();
    private static final BiFunction<String, Team, String> formatter = (tag, team) -> TextUtil.color(tag)
            .replace("{team}", team.getName())
            .replace("{team_color}", team.getTeamColor().getChatColor().toString())
            .replace("{team_letter}", team.getLetter());

    public static GameScoreboard getOrCreate(Player player) {
        if (scoreboards.containsKey(player.getUniqueId())) {
            return scoreboards.get(player.getUniqueId());
        } else {
            return new GameScoreboard(player);
        }
    }

    public static void remove(Player player) {
        scoreboards.remove(player.getUniqueId());
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    private final Player player;
    private final Scoreboard scoreboard;
    private final Objective objective;

    private GameScoreboard(Player player) {
        this.player = player;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("woolwars", "dummy");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        // Populate scoreboard with teams
        for (int i = 1; i <= 15; i++) {
            org.bukkit.scoreboard.Team team = scoreboard.registerNewTeam("line_" + i);
            team.addEntry(generateEntry(i));
        }
        scoreboards.put(player.getUniqueId(), this);
    }

    public void show() {
        player.setScoreboard(scoreboard);
    }

    public void hide() {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    public void setTitle(String title) {
        String color = TextUtil.color(title);
        objective.setDisplayName(color.length() > 32 ? color.substring(0, 32) : color);
    }

    public void setNameTags(Team team) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            getOrCreate(player).setNameTags(this.player, team);
        }
    }

    public void removeNameTags(Team team) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            GameScoreboard scoreboard = getOrCreate(player);
            if (scoreboard == null) continue;
            scoreboard.removeNameTags(this.player, team);
        }
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
            setLine(slot--, line);
        }
    }

    private void setLine(int line, String text) {
        org.bukkit.scoreboard.Team team = scoreboard.getTeam("line_" + line);
        String entry = generateEntry(line);
        if (!scoreboard.getEntries().contains(entry)) {
            objective.getScore(entry).setScore(line);
        }
        String color = TextUtil.color(text);
        String prefix = getFirstSplit(color);
        String suffix = getFirstSplit(ChatColor.getLastColors(prefix) + getSecondSplit(color));
        team.setPrefix(prefix);
        team.setSuffix(suffix);
    }

    private void setNameTags(Player player, Team team) {
        org.bukkit.scoreboard.Team scoreboardTeam = scoreboard.getTeam(getTeamName(team));
        // Team doesn't exist, create it
        if (scoreboardTeam == null) {
            scoreboardTeam = scoreboard.registerNewTeam(getTeamName(team));
            scoreboardTeam.setCanSeeFriendlyInvisibles(false);
            if (ReflectionUtil.supports(12)) {
                scoreboardTeam.setColor(team.getTeamColor().getChatColor());
            }
        }
        String prefix = formatter.apply(Config.NAME_TAGS_PREFIX.asString(), team);
        String suffix = formatter.apply(Config.NAME_TAGS_SUFFIX.asString(), team);
        // Set prefix and suffix, add player to team
        scoreboardTeam.setPrefix(getFirstSplit(prefix));
        scoreboardTeam.setSuffix(getFirstSplit(suffix));
        scoreboardTeam.addEntry(player.getName());
    }

    private void removeNameTags(Player player, Team team) {
        org.bukkit.scoreboard.Team scoreboardTeam = scoreboard.getTeam(getTeamName(team));
        if (scoreboardTeam == null) return;
        scoreboardTeam.removeEntry(player.getName());
        if (scoreboardTeam.getSize() == 0) {
            scoreboardTeam.unregister();
        }
    }

    private String generateEntry(int slot) {
        return ChatColor.values()[slot].toString();
    }

    private String getFirstSplit(String text) {
        String split = text.length() > 16 ? text.substring(0, 16) : text;
        // We remove the color character if it's the last character
        return split.endsWith("§") ? split.substring(0, split.length() - 1) : split;
    }

    private String getSecondSplit(String text) {
        if (text.length() > 32) {
            text = text.substring(0, 32);
        }
        // Fixes the color character being cut off
        boolean previousCode = text.length() > 16 && text.charAt(15) == '§';
        String split = text.length() > 16 ? text.substring(16) : "";
        return previousCode ? split.length() == 16 ? '§' + split.substring(0, split.length() - 1) : '§' + split : split;
    }

    private String getTeamName(Team team) {
        String name = team.getArena().getTeams().indexOf(team) + "_" + team.getArena().getId();
        return name.length() > 16 ? name.substring(0, 16) : name;
    }

}
