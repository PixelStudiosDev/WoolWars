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

import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.ArenaUtil;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.menu.game.KitsMenu;
import me.cubecrafter.woolwars.storage.player.PlayerManager;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        Arena arena = ArenaUtil.getArenaByPlayer(PlayerManager.get(event.getPlayer()));
        if (arena == null) return;
        if (arena.getState() != GameState.ACTIVE_ROUND) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        WoolPlayer woolPlayer = PlayerManager.get(player);
        Arena arena = ArenaUtil.getArenaByPlayer(woolPlayer);
        if (arena == null) return;

        if (arena.getState() == GameState.PRE_ROUND) {
            new KitsMenu(woolPlayer, arena).open();
            return;
        }

        if (player.getSpectatorTarget() == null || woolPlayer.isAlive()) {
            // The player is not spectating anyone
            return;
        }
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (ArenaUtil.isPlaying(PlayerManager.get(event.getPlayer()))) {
            event.setCancelled(true);
        }
    }

}
