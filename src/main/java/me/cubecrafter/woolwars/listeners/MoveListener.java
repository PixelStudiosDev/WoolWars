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
import me.cubecrafter.woolwars.api.events.player.PlayerPowerUpEvent;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.ArenaUtil;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.powerup.PowerUp;
import me.cubecrafter.woolwars.storage.player.PlayerManager;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.xutils.Events;
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
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;

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

        Block block = event.getTo().getBlock();

        if (arena.getState() == GameState.WAITING || arena.getState() == GameState.STARTING) {
            if (XBlock.isLava(block.getType())) {
                player.teleport(arena.getLobby());
                player.playSound(Config.SOUNDS_TELEPORT_TO_BASE.asString());
                return;
            }
        }

        if (player.isAlive()) {

            if (arena.getJumpPads().contains(block.getRelative(BlockFace.DOWN).getLocation())) {
                player.getPlayer().setVelocity(player.getLocation().getDirection().normalize().multiply(Config.JUMP_PADS_HORIZONTAL_POWER.asDouble()).setY(Config.JUMP_PADS_VERTICAL_POWER.asDouble()));
                player.playSound(Config.SOUNDS_JUMP_PAD.asString());
            }

            if (arena.getState() != GameState.ACTIVE_ROUND) return;

            for (PowerUp powerUp : arena.getPowerUps()) {
                if (!powerUp.isActive()) continue;

                double distance = player.getLocation().distance(powerUp.getLocation());

                if (distance <= 1.25) {
                    int delay = Config.POWERUP_ACTIVATION_DELAY.asInt();
                    if (arena.getCurrentTask().getSecondsElapsed() < delay) {
                        player.send(Messages.CANT_COLLECT_POWERUP.asString().replace("{seconds}", String.valueOf(delay)));
                        return;
                    }
                    if (!Events.call(new PlayerPowerUpEvent(player, powerUp, arena))) {
                        powerUp.use(player);
                    }
                }
            }
        }
    }

}
