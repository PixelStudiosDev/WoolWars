/*
 * Copyright (c) 2019 Jonah Seguin.  All rights reserved.  You may not modify, decompile, distribute or use any code/text contained in this document(plugin) without explicit signed permission from Jonah Seguin.
 */

package me.cubecrafter.woolwars.utils.scoreboard.view;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

@Getter
@Setter
public class ViewEntry {

    private final View view;
    private String text;
    private String id;
    private Team team;

    public ViewEntry(View view, String text) {
        this.view = view;
        this.text = text;
        this.id = view.getUniqueID();

        this.setup();
    }

    public void setup() {
        final Scoreboard b = this.view.getAbsorb().getScoreboard();
        if (b != null) {

            String teamName = this.id;
            if (teamName.length() > 16) {
                teamName = teamName.substring(0, 16);
            }

            Team team = b.getTeam(teamName);

            if (team == null) {
                team = b.registerNewTeam(teamName);
            }

            if (!team.getEntries().contains(this.id)) {
                team.addEntry(this.id);
            }

            if (!this.view.getEntries().contains(this)) {
                this.view.getEntries().add(this);
            }

            this.team = team;
        }
    }

    public void render(int lineNumber) {
        if (this.text.length() > 16) {
            String prefix = this.text.substring(0, 16);
            String suffix;

            if (prefix.charAt(15) == ChatColor.COLOR_CHAR) {
                prefix = prefix.substring(0, 15);
                suffix = this.text.substring(15);
            } else if (prefix.charAt(14) == ChatColor.COLOR_CHAR) {
                prefix = prefix.substring(0, 14);
                suffix = this.text.substring(14);
            } else {
                if (ChatColor.getLastColors(prefix).equalsIgnoreCase(ChatColor.getLastColors(this.id))) {
                    suffix = this.text.substring(16);
                } else {
                    suffix = ChatColor.getLastColors(prefix) + this.text.substring(16);
                }
            }

            if (suffix.length() > 16) {
                suffix = suffix.substring(0, 16);
            }

            this.team.setPrefix(prefix);
            this.team.setSuffix(suffix);
        } else {
            this.team.setPrefix(this.text);
            this.team.setSuffix("");
        }

        Score score = this.view.getAbsorb().getObjective().getScore(this.id);
        score.setScore(lineNumber);
    }

    public void remove() {
        this.view.getIdentifiers().remove(this.id);
        this.view.getAbsorb().getScoreboard().resetScores(this.id);
    }

}
