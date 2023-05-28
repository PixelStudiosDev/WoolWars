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

package me.cubecrafter.woolwars.listeners;

import me.cubecrafter.woolwars.arena.setup.SetupSession;
import me.cubecrafter.woolwars.arena.setup.TeamData;
import me.cubecrafter.woolwars.menu.setup.TeamSetupMenu;
import me.cubecrafter.woolwars.storage.player.PlayerManager;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class SetupListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        WoolPlayer player = PlayerManager.get(event.getPlayer());
        SetupSession session = SetupSession.get(player);
        if (session == null) return;

        Location location = event.getClickedBlock().getLocation();

        if (session.isSettingCenter()) {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                session.setCenterPos1(location);
                player.send("&aCenter position 1 set!");
                player.playSound("ORB_PICKUP");
            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                session.setCenterPos2(location);
                player.send("&aCenter position 2 set!");
                player.playSound("ORB_PICKUP");
            }
            // If both positions are set, open the menu again
            if (session.isCenterPos1Set() && session.isCenterPos2Set()) {
                session.setSettingCenter(false);
                session.getMenu().open();
            }
            event.setCancelled(true);

        } else if (session.getCurrentTeam() != null) {
            TeamData team = session.getCurrentTeam();

            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                team.setBarrierPos1(location);
                player.send("&aBarrier position 1 set!");
                player.playSound("ORB_PICKUP");
            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                team.setBarrierPos2(location);
                player.send("&aBarrier position 2 set!");
                player.playSound("ORB_PICKUP");
            }
            // If both positions are set, open the team setup menu again
            if (team.isBarrierPos1Set() && team.isBarrierPos2Set()) {
                session.setCurrentTeam(null);
                new TeamSetupMenu(player, session, team).open();
            }
            event.setCancelled(true);
        }
    }

}
