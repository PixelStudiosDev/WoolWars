package me.cubecrafter.woolwars.arena.setup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.cubecrafter.woolwars.team.TeamColor;
import org.bukkit.Location;

@Getter
@Setter
@RequiredArgsConstructor
public class TeamData {

    private final TeamColor color;
    private String name;
    private Location spawn;
    private Location basePos1;
    private Location basePos2;
    private Location barrierPos1;
    private Location barrierPos2;

    public boolean isNameSet() {
        return name != null;
    }

    public boolean isSpawnSet() {
        return spawn != null;
    }

    public boolean isBasePos1Set() {
        return basePos1 != null;
    }

    public boolean isBasePos2Set() {
        return basePos2 != null;
    }

    public boolean isBarrierPos1Set() {
        return barrierPos1 != null;
    }

    public boolean isBarrierPos2Set() {
        return barrierPos2 != null;
    }

    public boolean isValid() {
        return isNameSet() && isSpawnSet() && isBasePos1Set() && isBasePos2Set() && isBarrierPos1Set() && isBarrierPos2Set();
    }

}
