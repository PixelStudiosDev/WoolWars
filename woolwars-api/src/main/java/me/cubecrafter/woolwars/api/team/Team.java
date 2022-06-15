package me.cubecrafter.woolwars.api.team;

import me.cubecrafter.woolwars.api.arena.Arena;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public interface Team {
    void addMember(Player player);

    void removeMember(Player player);

    List<Player> getMembers();

    Arena getArena();

    Location getSpawnLocation();

    String getTeamLetter();

    void applyNameTags();

    void teleportToSpawn();

    void addPoint();

    void removeBarrier();

    void spawnBarrier();

    void reset();

    TeamColor getTeamColor();

}
