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

import com.cryptomorin.xseries.XMaterial;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.storage.PlayerData;
import me.cubecrafter.woolwars.powerup.PowerUp;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getFrom().getBlockX() == e.getTo().getBlockX() && e.getFrom().getBlockZ() == e.getTo().getBlockZ() && e.getFrom().getBlockY() == e.getTo().getBlockY()) return;
        Player player = e.getPlayer();
        if (!ArenaUtil.isPlaying(player)) return;
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (!arena.getArenaRegion().isInside(e.getTo())) {
            if (arena.isDead(player)) {
                player.teleport(arena.getLobby());
                ArenaUtil.playSound(player, Config.SOUNDS_TELEPORT_TO_BASE.getAsString());
            } else {
                switch (arena.getGameState()) {
                    case ACTIVE_ROUND:
                        ArenaUtil.handleDeath(player, arena);
                        break;
                    case WAITING:
                    case STARTING:
                    case ROUND_OVER:
                    case GAME_ENDED:
                        player.teleport(arena.getLobby());
                        ArenaUtil.playSound(player, Config.SOUNDS_TELEPORT_TO_BASE.getAsString());
                        break;
                    case PRE_ROUND:
                        player.teleport(arena.getTeamByPlayer(player).getSpawnLocation());
                        ArenaUtil.playSound(player, Config.SOUNDS_TELEPORT_TO_BASE.getAsString());
                        break;
                }
            }
        }
        if (arena.getGameState() == GameState.WAITING || arena.getGameState() == GameState.STARTING) {
            if (player.getLocation().getBlock().getType().toString().contains("LAVA")) {
                player.teleport(arena.getLobby());
                ArenaUtil.playSound(player, Config.SOUNDS_TELEPORT_TO_BASE.getAsString());
            }
        } else if (arena.getGameState() == GameState.ACTIVE_ROUND && !arena.getDeadPlayers().contains(player)) {
            Block top = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
            Block bottom = top.getRelative(BlockFace.DOWN);
            if (top.getType().equals(XMaterial.matchXMaterial(Config.JUMP_PADS_TOP_BLOCK.getAsString()).get().parseMaterial())
                    && bottom.getType().equals(XMaterial.matchXMaterial(Config.JUMP_PADS_BOTTOM_BLOCK.getAsString()).get().parseMaterial())) {
                player.setVelocity(player.getLocation().getDirection().normalize().multiply(Config.JUMP_PADS_HORIZONTAL_POWER.getAsDouble()).setY(Config.JUMP_PADS_VERTICAL_POWER.getAsDouble()));
                ArenaUtil.playSound(player, Config.SOUNDS_JUMP_PAD.getAsString());
            }
            for (PowerUp powerUp : arena.getPowerUps()) {
                if (!powerUp.isActive()) continue;
                double distance = player.getLocation().distance(powerUp.getLocation());
                if (distance <= 1) {
                    powerUp.use(player);
                    PlayerData data = ArenaUtil.getPlayerData(player);
                    data.setPowerUpsCollected(data.getPowerUpsCollected() + 1);
                }
            }
        }
    }

}
