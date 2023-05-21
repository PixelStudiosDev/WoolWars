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

package me.cubecrafter.woolwars.party.provider;

import de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayer;
import de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayerManager;
import de.simonsator.partyandfriends.spigot.api.party.PartyManager;
import de.simonsator.partyandfriends.spigot.api.party.PlayerParty;
import me.cubecrafter.woolwars.party.PartyProvider;
import me.cubecrafter.woolwars.storage.player.PlayerManager;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.stream.Collectors;

public class PAFBungee implements PartyProvider {

    @Override
    public boolean hasParty(WoolPlayer player) {
        return getParty(player) != null;
    }

    @Override
    public boolean isLeader(WoolPlayer player) {
        PlayerParty party = getParty(player);
        if (party == null) return false;
        return party.getLeader().getUniqueId().equals(player.getPlayer().getUniqueId());
    }

    @Override
    public boolean isOnline(WoolPlayer player) {
        PlayerParty party = getParty(player);
        if (party == null) return false;
        return party.getAllPlayers().stream().allMatch(member -> Bukkit.getPlayer(member.getUniqueId()) != null);
    }

    @Override
    public int getSize(WoolPlayer player) {
        PlayerParty party = getParty(player);
        if (party == null) return 0;
        return party.getAllPlayers().size();
    }

    @Override
    public List<WoolPlayer> getMembers(WoolPlayer player) {
        PlayerParty party = getParty(player);
        if (party == null) return null;
        return party.getPlayers().stream().map(pafPlayer -> PlayerManager.get(Bukkit.getPlayer(pafPlayer.getUniqueId()))).collect(Collectors.toList());
    }

    private PlayerParty getParty(WoolPlayer player) {
        PAFPlayer pafPlayer = PAFPlayerManager.getInstance().getPlayer(player.getPlayer().getUniqueId());
        return PartyManager.getInstance().getParty(pafPlayer);
    }

}

