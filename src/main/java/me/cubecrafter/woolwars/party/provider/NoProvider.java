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

package me.cubecrafter.woolwars.party.provider;

import me.cubecrafter.woolwars.party.Party;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NoProvider implements PartyProvider {

    private final List<Party> parties = new ArrayList<>();

    @Override
    public Party getParty(Player player) {
        return parties.stream().filter(party -> party.isMember(player) || party.isLeader(player)).findAny().orElse(null);
    }

    @Override
    public Party createParty(Player leader) {
        Party party = new Party(leader);
        parties.add(party);
        return party;
    }

    @Override
    public boolean hasParty(Player player) {
        return getParty(player) != null;
    }

    @Override
    public void disbandParty(Party party) {
        parties.remove(party);
    }

}
