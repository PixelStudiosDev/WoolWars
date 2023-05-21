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

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.party.PartyProvider;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TeamAssigner {

    public void assign(Arena arena) {
        PartyProvider party = WoolWars.get().getPartyProvider();
        if (party != null && arena.getMaxPlayersPerTeam() > 1) {
            // Players without a party
            List<WoolPlayer> single = new ArrayList<>();
            // Parties
            Set<List<WoolPlayer>> parties = new HashSet<>();
            // Check which players are in a party
            for (WoolPlayer player : arena.getPlayers()) {
                if (party.hasParty(player)) {
                    parties.add(party.getMembers(player));
                } else {
                    single.add(player);
                }
            }
            // Sort parties from biggest to smallest
            for (List<WoolPlayer> members : parties.stream().sorted(Comparator.comparingInt((List<WoolPlayer> list) -> list.size()).reversed()).collect(Collectors.toList())) {
                for (Team team : arena.getTeams()) {
                    // Check if there is enough space in the team
                    if (members.size() > arena.getMaxPlayersPerTeam() - team.getSize()) continue;
                    members.forEach(team::addMember);
                }
            }
            // Assign players without a party to teams
            assignTeams(arena, single);
        } else {
            // There is only one player per team, or the party impl is null, so randomly assign players to teams
            assignTeams(arena, arena.getPlayers());
        }
    }

    private void assignTeams(Arena arena, Collection<WoolPlayer> players) {
        for (WoolPlayer player : players) {
            arena.getTeams().stream().min(Comparator.comparingInt(Team::getSize)).orElse(arena.getTeams().get(0)).addMember(player);
        }
    }

}
