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

package me.cubecrafter.woolwars.listeners;

import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.menu.menus.TeleporterMenu;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

public class InteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            for (String material : Config.DISABLED_INTERACTION_BLOCKS.getAsStringList()) {
                if (TextUtil.matchMaterial(material, e.getClickedBlock().getType().toString())) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack item = e.getItem();
        if (item == null) return;
        if (item.getItemMeta() instanceof PotionMeta && arena.getGameState() != GameState.ACTIVE_ROUND) {
            e.setCancelled(true);
        }
        String tag = ItemBuilder.getTag(item);
        if (tag == null) return;
        switch (tag) {
            case "teleport-item":
                e.setCancelled(true);
                new TeleporterMenu(player, arena).openMenu();
                break;
            case "leave-item":
                e.setCancelled(true);
                arena.removePlayer(player, true);
                break;
            case "ability-item":
                e.setCancelled(true);
                ArenaUtil.getKitByPlayer(player).getAbility().use(player, arena);
                break;
            case "playagain-item":
                e.setCancelled(true);
                if (!player.hasPermission("woolwars.playagain")) {
                    TextUtil.sendMessage(player, Messages.NO_PERMISSION.getAsString());
                    break;
                }
                arena.removePlayer(player, false);
                if (!ArenaUtil.joinRandomArena(player, arena.getGroup())) {
                    ArenaUtil.teleportToLobby(player);
                }
                break;
        }
    }

}
