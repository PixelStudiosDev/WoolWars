package me.cubecrafter.woolwars.core;

import lombok.Getter;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Team {

    @Getter private final String name;
    @Getter private final Location spawnLocation;
    @Getter private final TeamColor teamColor;
    @Getter private final List<Player> members = new ArrayList<>();
    @Getter private final String teamLetter;
    @Getter private int points;
    private final org.bukkit.scoreboard.Team scoreboardTeam;

    public Team(String name, Location spawnLocation, TeamColor color) {
        this.name = name;
        this.spawnLocation = spawnLocation;
        this.teamColor = color;
        this.teamLetter = name.substring(0,1).toUpperCase();
        this.scoreboardTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(UUID.randomUUID().toString());
        scoreboardTeam.setAllowFriendlyFire(false);
        // TODO use packets for colored player name scoreboardTeam.setPrefix(TextUtil.color("&l" + getTeamLetter() + "&r "));
    }

    public void addMember(Player player) {
        members.add(player);
    }

    public void removeMember(Player player) {
        members.remove(player);
    }

    public void setNameTags() {
        for (Player player : getMembers()) {
            scoreboardTeam.addEntry(player.getName());
            player.setDisplayName(TextUtil.color(getTeamColor().getChatColor() + "&l" + getTeamLetter() + " " + getTeamColor().getChatColor() + player.getName()));
        }
    }

    public void teleportToSpawn() {
        for (Player player : getMembers()) {
            player.teleport(spawnLocation);
        }
    }

    public void addPoint() {
        points++;
    }

    public void resetPoints() {
        points = 0;
    }

}
