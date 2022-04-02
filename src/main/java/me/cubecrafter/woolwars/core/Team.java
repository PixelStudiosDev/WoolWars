package me.cubecrafter.woolwars.core;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
        this.scoreboardTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(String.valueOf(new Random().nextInt()));
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
            player.setPlayerListName(TextUtil.color(getTeamColor().getChatColor() + "&l" + getTeamLetter() + " " + getTeamColor().getChatColor() + player.getName()));
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

    public void applyArmor() {
        ItemStack helmet = new ItemBuilder("LEATHER_HELMET").setColor(teamColor.getColor()).build();
        ItemStack chestplate = new ItemBuilder("LEATHER_CHESTPLATE").setColor(teamColor.getColor()).build();
        ItemStack leggins = new ItemBuilder("LEATHER_LEGGINGS").setColor(teamColor.getColor()).build();
        ItemStack boots = new ItemBuilder("LEATHER_BOOTS").setColor(teamColor.getColor()).build();
        for (Player player : members) {
            player.getInventory().setHelmet(helmet);
            player.getInventory().setChestplate(chestplate);
            player.getInventory().setLeggings(leggins);
            player.getInventory().setBoots(boots);
        }
    }

}
