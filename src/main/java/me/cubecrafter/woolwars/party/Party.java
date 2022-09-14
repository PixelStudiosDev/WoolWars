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

package me.cubecrafter.woolwars.party;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.WoolWars;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Party {

    private final Player leader;
    private final List<UUID> members = new ArrayList<>();

    public List<Player> getOnlineMembers() {
        List<Player> online = new ArrayList<>();
        for (UUID member : members) {
            Player player = Bukkit.getPlayer(member);
            if (player == null) continue;
            online.add(player);
        }
        return online;
    }

    public void addMember(UUID uuid) {
        members.add(uuid);
    }

    public void removeMember(UUID uuid) {
        members.remove(uuid);
    }

    public boolean isMember(Player player) {
        return members.contains(player.getUniqueId());
    }

    public boolean isLeader(Player player) {
        return leader.equals(player);
    }

    public int getSize() {
        return members.size() + 1;
    }

    public void disband() {
        WoolWars.getInstance().getPartyProvider().disbandParty(this);
    }

}
