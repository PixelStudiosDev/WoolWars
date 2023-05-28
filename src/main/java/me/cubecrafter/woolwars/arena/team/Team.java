/*
 * Wool Wars
 * Copyright (C) 2023 CubeCrafter Development
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

package me.cubecrafter.woolwars.arena.team;

import lombok.Getter;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.woolwars.utils.Cuboid;
import me.cubecrafter.woolwars.arena.TabHandler;
import me.cubecrafter.xutils.TextUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Team {

    private final Arena arena;

    private final List<WoolPlayer> members = new ArrayList<>();
    private final TeamColor teamColor;
    private final String name;
    private final Location spawn;

    private final Cuboid barrier;
    private final Cuboid base;

    private int points;

    public Team(Arena arena, ConfigurationSection section) {
        this.arena = arena;
        this.teamColor = TeamColor.valueOf(section.getName().toUpperCase());
        this.name = section.getString("name");
        this.spawn = TextUtil.parseLocation(section.getString("spawn-location"));
        this.barrier = new Cuboid(
                TextUtil.parseLocation(section.getString("barrier.pos1")),
                TextUtil.parseLocation(section.getString("barrier.pos2"))
        );
        this.base = new Cuboid(
                TextUtil.parseLocation(section.getString("base.pos1")),
                TextUtil.parseLocation(section.getString("base.pos2"))
        );
    }

    public void addMember(WoolPlayer player) {
        members.add(player);
    }

    public void removeMember(WoolPlayer player) {
        members.remove(player);
        TabHandler.removeTags(player.getPlayer(), this);
    }

    public boolean isMember(WoolPlayer player) {
        return members.contains(player);
    }

    public String getLetter() {
        return name.substring(0,1).toUpperCase();
    }

    public int getSize() {
        return members.size();
    }

    public void updateNameTags() {
        if (Config.NAME_TAGS_ENABLED.asBoolean()) {
            members.forEach(member -> TabHandler.applyTags(member.getPlayer(), this));
        }
    }

    public void newRound() {
        // Respawn players
        for (WoolPlayer member : members) {
            member.teleport(spawn);
            member.playSound(Config.SOUNDS_TELEPORT_TO_BASE.asString());
        }
        // Reset barrier
        barrier.fill("GLASS");
    }

    public int addPoint() {
        return ++points;
    }

    public void broadcast(String message) {
        members.forEach(player -> player.send(message));
    }

    public void broadcast(List<String> messages) {
        members.forEach(player -> player.send(messages));
    }

    public void playSound(String sound) {
        members.forEach(player -> player.playSound(sound));
    }

    public void sendTitle(String title, String subtitle, int stay) {
        members.forEach(player -> player.sendTitle(title, subtitle, stay));
    }

    public void removeBarrier() {
        barrier.fill("AIR");
    }

    public int getAliveCount() {
        return (int) members.stream().filter(WoolPlayer::isAlive).count();
    }

    public void reset() {
        members.clear();
        points = 0;
        if (Config.NAME_TAGS_ENABLED.asBoolean()) {
            members.forEach(member -> TabHandler.removeTags(member.getPlayer(), this));
        }
    }

}
