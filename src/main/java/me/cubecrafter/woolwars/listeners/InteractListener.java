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

import com.cryptomorin.xseries.XBlock;
import me.cubecrafter.woolwars.api.events.player.PlayerLeaveArenaEvent;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.ArenaUtil;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.menu.game.TeleporterMenu;
import me.cubecrafter.woolwars.storage.player.PlayerManager;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.woolwars.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        WoolPlayer player = PlayerManager.get(event.getPlayer());
        Arena arena = ArenaUtil.getArenaByPlayer(player);

        if (arena == null) return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (XBlock.isOneOf(event.getClickedBlock(), Config.DISABLED_INTERACTION_BLOCKS.asStringList())) {
                event.setCancelled(true);
                return;
            }
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        if (item == null) return;

        // Disable potions if the game is not active
        if (item.getType().toString().contains("POTION") && arena.getState() != GameState.ACTIVE_ROUND) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
            return;
        }

        String tag = Utils.getTag(item);
        if (tag.isEmpty()) return;

        event.setCancelled(true);
        switch (tag) {
            case "teleporter":
                new TeleporterMenu(player, arena).open();
                break;
            case "leave":
                arena.removePlayer(player, PlayerLeaveArenaEvent.Reason.QUIT);
                break;
            case "ability":
                player.getSelectedKit().getAbility().use(player, arena);
                break;
            case "playagain":
                if (!player.hasPermission("woolwars.playagain")) {
                    player.send(Messages.NO_PERMISSION.asString());
                    break;
                }
                arena.removePlayer(player, PlayerLeaveArenaEvent.Reason.PLAY_AGAIN);
                if (!ArenaUtil.joinRandomArena(player, arena.getGroup())) {
                    player.teleportToLobby();
                }
                break;
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player)) return;
        Player player = event.getPlayer();
        WoolPlayer woolPlayer = PlayerManager.get(player);
        Arena arena = ArenaUtil.getArenaByPlayer(woolPlayer);
        if (arena == null) return;
        if (woolPlayer.isAlive()) return;
        Player clicked = (Player) event.getRightClicked();
        Arena other = ArenaUtil.getArenaByPlayer(PlayerManager.get(clicked));
        if (!arena.equals(other)) return;
        player.setGameMode(GameMode.SPECTATOR);
        player.setSpectatorTarget(clicked);
    }

}
