package me.cubecrafter.woolwars.core;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private final String name;
    private final Location spawnLocation;
    private final Color color;
    private final List<Player> members = new ArrayList<>();

    public Team(String name, Color color, Location spawnLocation) {
        this.name = name;
        this.color = color;
        this.spawnLocation = spawnLocation;
    }

    public void addMember(Player player) {
        members.add(player);
    }

    public String getName() {
        return name;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public Color getColor() {
        return color;
    }

    public List<Player> getMembers() {
        return members;
    }

}
