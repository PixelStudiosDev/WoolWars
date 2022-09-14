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
import me.cubecrafter.woolwars.menu.menus.KitsMenu;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class ArenaListener implements Listener {

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        if (ArenaUtil.getArenas().stream().anyMatch(arena -> arena.getArenaRegion().isInside(e.getLocation()))) {
            if (e.getEntityType() == EntityType.ARMOR_STAND) return;
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (ArenaUtil.getArenas().stream().anyMatch(arena -> arena.getArenaRegion().isInsideWithMarge(e.getLocation(), 10))) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        Projectile projectile = e.getEntity();
        if (projectile.getShooter() instanceof Player) {
            Player player = (Player) projectile.getShooter();
            if (!ArenaUtil.isPlaying(player)) return;
            projectile.remove();
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if (ArenaUtil.isPlaying(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent e) {
        if (ArenaUtil.isPlaying((Player) e.getEntity())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        if (arena.getGameState() != GameState.ACTIVE_ROUND) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpectate(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        if (!arena.getDeadPlayers().contains(player)) return;
        if (!(e.getRightClicked() instanceof Player)) return;
        Player clicked = (Player) e.getRightClicked();
        Arena other = ArenaUtil.getArenaByPlayer(clicked);
        if (!arena.equals(other)) return;
        player.setGameMode(GameMode.SPECTATOR);
        player.setSpectatorTarget(clicked);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        if (arena.getGameState() == GameState.PRE_ROUND) {
            new KitsMenu(player, arena).openMenu();
        }
        if (!arena.getDeadPlayers().contains(player)) return;
        if (player.getSpectatorTarget() == null) return;
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent e) {
        Player player = e.getPlayer();
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        if (arena.getGameState() != GameState.ACTIVE_ROUND) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        e.setCancelled(true);
    }

}
