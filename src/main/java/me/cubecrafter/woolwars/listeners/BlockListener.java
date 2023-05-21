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
import me.cubecrafter.woolwars.arena.tasks.RoundTask;
import me.cubecrafter.woolwars.arena.team.Team;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.storage.player.PlayerManager;
import me.cubecrafter.woolwars.storage.player.StatisticType;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.woolwars.utils.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        WoolPlayer player = PlayerManager.get(event.getPlayer());
        if (!ArenaUtil.isPlaying(player)) return;
        Block block = event.getBlock();
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena.getState() != GameState.ACTIVE_ROUND || !player.isAlive()) {
            event.setCancelled(true);
            player.send(Messages.CANT_BREAK_BLOCK.asString());
        } else if (arena.getCenterRegion().isInside(block.getLocation())) {
            if (arena.isCenterLocked()) {
                player.send(Messages.CENTER_LOCKED.asString());
                event.setCancelled(true);
                return;
            }
            RoundTask task = (RoundTask) arena.getCurrentTask();
            task.removePlacedWool(block);
            block.setType(Material.AIR);
            player.getData().addRoundStatistic(StatisticType.BLOCKS_BROKEN, 1);
        } else if (!arena.getPlacedBlocks().contains(block)) {
            event.setCancelled(true);
            player.send(Messages.CANT_BREAK_BLOCK.asString());
        } else {
            arena.getPlacedBlocks().remove(block);
            block.setType(Material.AIR);
            player.getData().addRoundStatistic(StatisticType.BLOCKS_BROKEN, 1);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        WoolPlayer player = PlayerManager.get(event.getPlayer());

        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        Block block = event.getBlock();

        if (arena.getState() == GameState.ACTIVE_ROUND &&
            event.getBlockReplacedState().getType() == Material.AIR && !ArenaUtil.isBlockInTeamBase(block, arena)) {

            if (arena.getCenterRegion().isInside(block.getLocation())) {
                if (arena.isCenterLocked()) {
                    player.send(Messages.CENTER_LOCKED.asString());
                    event.setCancelled(true);
                    return;
                }

                if (block.getType().toString().contains("WOOL")) {
                    Team team = arena.getTeam(player);
                    RoundTask roundTask = (RoundTask) arena.getCurrentTask();
                    player.getData().addRoundStatistic(StatisticType.WOOL_PLACED, 1);
                    roundTask.addPlacedWool(team, block);
                    return;
                }
            }

            for (String material : Config.PLACEABLE_BLOCKS.asStringList()) {
                if (Utils.matchMaterial(material, block.getType().toString())) {
                    arena.getPlacedBlocks().add(block);
                    return;
                }
            }
        }

        player.send(Messages.CANT_PLACE_BLOCK.asString());
        event.setCancelled(true);
    }

}
