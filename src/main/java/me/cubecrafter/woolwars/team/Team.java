/*
 * Wool Wars
 * Copyright (C) 2022 CubeCrafter Development
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.cubecrafter.woolwars.team;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.Cuboid;
import me.cubecrafter.woolwars.utils.PlayerScoreboard;
import me.cubecrafter.woolwars.utils.VersionUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class Team {

    private final List<Player> members = new ArrayList<>();
    private final String name;
    private final Arena arena;
    private final Location spawnLocation;
    private final TeamColor teamColor;
    private final Cuboid barrier;
    private final Cuboid base;
    private int points;

    public void addMember(Player player) {
        members.add(player);
    }

    public void removeMember(Player player) {
        PlayerScoreboard.getOrCreate(player).removeGamePrefix(this);
        members.remove(player);
    }

    public String getTeamLetter() {
        return name.substring(0,1).toUpperCase();
    }

    public void applyNameTags() {
        if (!Config.NAME_TAGS_ENABLED.getAsBoolean()) return;
        for (Player player : getMembers()) {
            PlayerScoreboard.getOrCreate(player).setGamePrefix(this);
        }
    }

    public void respawn() {
        members.forEach(member -> {
            member.teleport(spawnLocation);
            VersionUtil.setCollidable(member, false);
        });
        ArenaUtil.playSound(members, Config.SOUNDS_TELEPORT_TO_BASE.getAsString());
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
        if (Config.NAME_TAGS_ENABLED.getAsBoolean()) {
            for (Player player : members) {
                PlayerScoreboard.getOrCreate(player).removeGamePrefix(this);
            }
        }
        points = 0;
        members.clear();
    }

}
