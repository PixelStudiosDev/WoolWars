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

package me.cubecrafter.woolwars.arena;

import me.cubecrafter.woolwars.arena.team.Team;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.xutils.ReflectionUtil;
import me.cubecrafter.xutils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.function.BiFunction;

public class TabHandler {

    private final BiFunction<String, Team, String> formatter = (tag, team) -> TextUtil.color(tag)
            .replace("{team}", team.getName())
            .replace("{team_color}", team.getTeamColor().getChatColor().toString())
            .replace("{team_letter}", team.getLetter());

    public void applyTags(Team team) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            setNameTags(online, team);
        }
    }

    public void removeTags(Team team) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            team.getMembers().forEach(member -> removeTags(online, member.getPlayer(), team));
        }
    }

    public void removeTags(Player player, Team team) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            removeTags(online, player, team);
        }
    }

    public void onQuit(Player player) {
        player.getScoreboard().getTeams().forEach(org.bukkit.scoreboard.Team::unregister);
    }

    private void setNameTags(Player viewer, Team team) {
        Scoreboard scoreboard = viewer.getScoreboard();
        org.bukkit.scoreboard.Team scoreboardTeam = scoreboard.getTeam(getTeamName(team));
        // Team doesn't exist, create it
        if (scoreboardTeam == null) {
            scoreboardTeam = scoreboard.registerNewTeam(getTeamName(team));
            scoreboardTeam.setCanSeeFriendlyInvisibles(false);
            // From 1.12 onwards, we can set the team color
            if (ReflectionUtil.supports(12)) {
                scoreboardTeam.setColor(team.getTeamColor().getChatColor());
            }
        }
        // Set prefix and suffix, add player to team
        scoreboardTeam.setPrefix(resize(formatter.apply(Config.NAME_TAGS_PREFIX.asString(), team)));
        scoreboardTeam.setSuffix(resize(formatter.apply(Config.NAME_TAGS_SUFFIX.asString(), team)));
        // Add all members to the team
        for (WoolPlayer member : team.getMembers()) {
            scoreboardTeam.addEntry(member.getName());
        }
    }

    private void removeTags(Player viewer, Player player, Team team) {
        org.bukkit.scoreboard.Team scoreboardTeam = viewer.getScoreboard().getTeam(getTeamName(team));
        if (scoreboardTeam == null) return;
        scoreboardTeam.removeEntry(player.getName());
        // Remove team if empty
        if (scoreboardTeam.getSize() == 0) {
            scoreboardTeam.unregister();
        }
    }

    private String getTeamName(Team team) {
        return resize(team.getArena().getTeams().indexOf(team) + "_" + team.getArena().getId());
    }

    private String resize(String text) {
        return text.length() > 16 ? text.substring(0, 16) : text;
    }

}
