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

import com.cryptomorin.xseries.XMaterial;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.ArenaUtil;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.powerup.PowerUp;
import me.cubecrafter.woolwars.storage.player.PlayerManager;
import me.cubecrafter.woolwars.storage.player.StatisticType;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ() &&
                event.getFrom().getBlockY() == event.getTo().getBlockY()) return;

        WoolPlayer player = PlayerManager.get(event.getPlayer());
        if (!ArenaUtil.isPlaying(player)) return;
        Arena arena = ArenaUtil.getArenaByPlayer(player);

        if (!arena.getArenaRegion().isInside(event.getTo())) {
            if (!player.isAlive()) {
                player.teleport(arena.getLobby());
                player.playSound(Config.SOUNDS_TELEPORT_TO_BASE.asString());
            } else {
                switch (arena.getState()) {
                    case ACTIVE_ROUND:
                        DamageListener.handleDeath(player, arena);
                        break;
                    case PRE_ROUND:
                        player.teleport(arena.getTeam(player).getSpawn());
                        player.playSound(Config.SOUNDS_TELEPORT_TO_BASE.asString());
                        break;
                    default:
                        player.teleport(arena.getLobby());
                        player.playSound(Config.SOUNDS_TELEPORT_TO_BASE.asString());
                        break;
                }
            }
            return;
        }

        if (arena.getState() == GameState.WAITING || arena.getState() == GameState.STARTING) {
            if (player.getLocation().getBlock().getType().toString().contains("LAVA")) {
                player.teleport(arena.getLobby());
                player.playSound(Config.SOUNDS_TELEPORT_TO_BASE.asString());
                return;
            }
        }

        if (player.isAlive()) {
            Block top = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
            Block bottom = top.getRelative(BlockFace.DOWN);
            if (top.getType().equals(XMaterial.matchXMaterial(Config.JUMP_PADS_TOP_BLOCK.asString()).get().parseMaterial())
                    && bottom.getType().equals(XMaterial.matchXMaterial(Config.JUMP_PADS_BOTTOM_BLOCK.asString()).get().parseMaterial())) {
                player.getPlayer().setVelocity(player.getLocation().getDirection().normalize().multiply(Config.JUMP_PADS_HORIZONTAL_POWER.asDouble()).setY(Config.JUMP_PADS_VERTICAL_POWER.asDouble()));
                player.playSound(Config.SOUNDS_JUMP_PAD.asString());
            }
            if (arena.getState() != GameState.ACTIVE_ROUND) return;
            for (PowerUp powerUp : arena.getPowerUps()) {
                if (!powerUp.isActive()) continue;
                double distance = player.getLocation().distance(powerUp.getLocation());
                if (distance <= 1) {
                    powerUp.use(player);
                    player.getData().addRoundStatistic(StatisticType.POWERUPS_COLLECTED, 1);
                }
            }
        }
    }

}
