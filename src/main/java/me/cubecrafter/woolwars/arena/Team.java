package me.cubecrafter.woolwars.arena;

import lombok.Getter;
import me.cubecrafter.woolwars.utils.Cuboid;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Team {

    private final List<Player> members = new ArrayList<>();
    private final String name;
    private final Arena arena;
    private final Location spawnLocation;
    private final TeamColor teamColor;
    private final Cuboid barrier;
    private final String barrierBlock;
    private int points;

    public Team(String name, Arena arena, Location spawnLocation, TeamColor color, Cuboid barrier) {
        this.name = name;
        this.arena = arena;
        this.spawnLocation = spawnLocation;
        this.teamColor = color;
        this.barrier = barrier;
        this.barrierBlock = "GLASS";
    }

    public void addMember(Player player) {
        members.add(player);
    }

    public void removeMember(Player player) {
        members.remove(player);
    }

    public String getTeamLetter() {
        return name.substring(0,1).toUpperCase();
    }

    public void setNameTags() {
        for (Player player : getMembers()) {
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

}
