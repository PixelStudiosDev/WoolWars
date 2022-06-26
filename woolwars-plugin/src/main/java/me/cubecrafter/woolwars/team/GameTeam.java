package me.cubecrafter.woolwars.team;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import me.cubecrafter.woolwars.api.team.Team;
import me.cubecrafter.woolwars.api.team.TeamColor;
import me.cubecrafter.woolwars.arena.GameArena;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.api.arena.Cuboid;
import me.cubecrafter.woolwars.utils.PlayerScoreboard;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GameTeam implements Team {

    private final List<Player> members = new ArrayList<>();
    private final String name;
    private final GameArena arena;
    private final Location spawnLocation;
    private final TeamColor teamColor;
    private final Cuboid barrier;
    private final Cuboid base;
    private int points;

    public GameTeam(String name, GameArena arena, Location spawnLocation, TeamColor color, Cuboid barrier, Cuboid base) {
        this.name = name;
        this.arena = arena;
        this.spawnLocation = spawnLocation;
        this.teamColor = color;
        this.barrier = barrier;
        this.base = base;
    }

    @Override
    public void addMember(Player player) {
        members.add(player);
    }

    @Override
    public void removeMember(Player player) {
        members.remove(player);
    }

    @Override
    public String getTeamLetter() {
        return name.substring(0,1).toUpperCase();
    }

    @Override
    public void applyNameTags() {
        for (Player player : getMembers()) {
            PlayerScoreboard scoreboard = PlayerScoreboard.getScoreboard(player);
            scoreboard.setGamePrefix(this);
        }
    }

    @Override
    public void teleportToSpawn() {
        members.forEach(member -> member.teleport(spawnLocation));
        ArenaUtil.playSound(members, Configuration.SOUNDS_TELEPORT_TO_BASE.getAsString());
    }

    @Override
    public void addPoint() {
        points++;
    }

    @Override
    public void removeBarrier() {
        barrier.fill(Material.AIR);
    }

    @Override
    public void spawnBarrier() {
        barrier.fill(XMaterial.GLASS.parseMaterial());
    }

    @Override
    public void reset() {
        for (Player player : members) {
            PlayerScoreboard.getScoreboard(player).removeGamePrefix(this);
        }
        points = 0;
        members.clear();
    }

}
