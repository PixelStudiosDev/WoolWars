package me.cubecrafter.woolwars.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class Team {

    private final String name;
    private final Location spawnLocation;
    private final Color color;
    private final List<Player> members = new ArrayList<>();

    public void addMember(Player player) {
        members.add(player);
    }

}
