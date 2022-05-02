package me.cubecrafter.woolwars.game.team;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.Titles;
import lombok.Getter;
import me.cubecrafter.woolwars.game.arena.Arena;
import me.cubecrafter.woolwars.utils.Cuboid;
import me.cubecrafter.woolwars.utils.GameScoreboard;
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
    private final Cuboid base;
    private final String barrierBlock;
    private int points;

    public Team(String name, Arena arena, Location spawnLocation, TeamColor color, Cuboid barrier, Cuboid base) {
        this.name = name;
        this.arena = arena;
        this.spawnLocation = spawnLocation;
        this.teamColor = color;
        this.barrier = barrier;
        this.barrierBlock = "GLASS";
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

    public void setNameTags() {
        for (Player player : getMembers()) {
            GameScoreboard scoreboard = GameScoreboard.getScoreboard(player);
            scoreboard.setGamePrefix(this);
        }
    }

    public void teleportToSpawn() {
        for (Player player : members) {
            player.teleport(spawnLocation);
        }
        playSound("ENDERMAN_TELEPORT");
    }

    public void addPoint() {
        points++;
    }

    public void removeBarrier() {
        barrier.fill("AIR");
    }

    public void spawnBarrier() {
        barrier.fill(barrierBlock);
    }

    public void reset() {
        for (Player player : members) {
            GameScoreboard scoreboard = GameScoreboard.getScoreboard(player);
            scoreboard.removeGamePrefix(this);
        }
        points = 0;
        members.clear();
    }

    public void sendMessage(String msg) {
        members.forEach(player -> player.sendMessage(TextUtil.color(msg)));
    }

    public void sendTitle(int stay, String title, String subtitle) {
        members.forEach(player -> Titles.sendTitle(player, 0, stay, 0, TextUtil.color(title), TextUtil.color(subtitle)));
    }

    public void playSound(String sound) {
        members.forEach(player -> XSound.play(player, sound));
    }

}
