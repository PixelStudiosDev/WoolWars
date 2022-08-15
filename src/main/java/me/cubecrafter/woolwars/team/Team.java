package me.cubecrafter.woolwars.team;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.Cuboid;
import me.cubecrafter.woolwars.utils.PlayerScoreboard;
import org.bukkit.Location;
import org.bukkit.Material;
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
    private final Cuboid base;
    private int points;

    public Team(String name, Arena arena, Location spawnLocation, TeamColor color, Cuboid barrier, Cuboid base) {
        this.name = name;
        this.arena = arena;
        this.spawnLocation = spawnLocation;
        this.teamColor = color;
        this.barrier = barrier;
        this.base = base;
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

    public void applyNameTags() {
        for (Player player : getMembers()) {
            PlayerScoreboard scoreboard = PlayerScoreboard.getScoreboard(player);
            scoreboard.setGamePrefix(this);
        }
    }

    public void teleportToSpawn() {
        members.forEach(member -> member.teleport(spawnLocation));
        ArenaUtil.playSound(members, Configuration.SOUNDS_TELEPORT_TO_BASE.getAsString());
    }

    public void addPoint() {
        points++;
    }

    public void removeBarrier() {
        barrier.fill(Material.AIR);
    }

    public void spawnBarrier() {
        barrier.fill(XMaterial.GLASS.parseMaterial());
    }

    public void reset() {
        for (Player player : members) {
            PlayerScoreboard.getScoreboard(player).removeGamePrefix(this);
        }
        points = 0;
        members.clear();
    }

}
