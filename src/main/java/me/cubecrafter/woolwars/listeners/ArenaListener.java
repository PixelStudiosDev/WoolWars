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

import me.cubecrafter.woolwars.api.events.player.PlayerJoinArenaEvent;
import me.cubecrafter.woolwars.api.events.player.PlayerKillEvent;
import me.cubecrafter.woolwars.api.events.player.PlayerLeaveArenaEvent;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.ArenaUtil;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.arena.team.Team;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.menu.game.KitsMenu;
import me.cubecrafter.woolwars.menu.game.TeleporterMenu;
import me.cubecrafter.woolwars.storage.player.PlayerManager;
import me.cubecrafter.woolwars.storage.player.StatisticType;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.woolwars.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;

public class ArenaListener implements Listener {

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (ArenaUtil.getArenas().stream().anyMatch(arena -> arena.getWorld().equals(event.getLocation().getWorld()))) {
            if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (ArenaUtil.getArenas().stream().anyMatch(arena -> arena.getWorld().equals(event.getLocation().getWorld()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile.getShooter() instanceof Player) {
            Player player = (Player) projectile.getShooter();
            if (!ArenaUtil.isPlaying(PlayerManager.get(player))) return;
            projectile.remove();
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (ArenaUtil.isPlaying(PlayerManager.get(event.getPlayer()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (ArenaUtil.isPlaying(PlayerManager.get((Player) event.getEntity()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Arena arena = ArenaUtil.getArenaByPlayer(PlayerManager.get(player));
        if (arena == null) return;
        if (arena.getState() != GameState.ACTIVE_ROUND) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
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

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        WoolPlayer woolPlayer = PlayerManager.get(player);
        Arena arena = ArenaUtil.getArenaByPlayer(woolPlayer);
        if (arena == null) return;
        if (arena.getState() == GameState.PRE_ROUND) {
            new KitsMenu(woolPlayer, arena).open();
        }
        if (!woolPlayer.isAlive()) return;
        if (player.getSpectatorTarget() == null) {
            // The player is not spectating anyone
            return;
        }
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        Arena arena = ArenaUtil.getArenaByPlayer(PlayerManager.get(event.getPlayer()));
        if (arena == null) return;
        if (arena.getState() != GameState.ACTIVE_ROUND) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onArenaJoin(PlayerJoinArenaEvent event) {
        WoolPlayer player = event.getPlayer();
        Arena arena = event.getArena();
        for (WoolPlayer online : PlayerManager.getOnline()) {
            player.setVisibility(online, arena.isPlaying(online));
            online.setVisibility(player, arena.isPlaying(online));
        }
    }

    @EventHandler
    public void onArenaLeave(PlayerLeaveArenaEvent event) {
        WoolPlayer player = event.getPlayer();
        Arena arena = event.getArena();
        for (WoolPlayer online : PlayerManager.getOnline()) {
            player.setVisibility(online, !arena.isPlaying(online));
            online.setVisibility(player, !arena.isPlaying(online));
        }
    }

    @EventHandler
    public void onKill(PlayerKillEvent event) {
        Arena arena = event.getArena();
        WoolPlayer victim = event.getVictim();
        WoolPlayer attacker = event.getAttacker();

        String message = Messages.DEATH_GENERIC.asString();

        switch (event.getCause()) {
            case PVP: case PROJECTILE:
                if (attacker == null) break;
                attacker.getData().addRoundStatistic(StatisticType.KILLS, 1);
                message = Messages.KILL_MESSAGE.asString();
                break;
            case FALL:
                message = Messages.DEATH_BY_FALL.asString();
                break;
            case VOID:
                message = Messages.DEATH_BY_VOID.asString();
                break;
            case LAVA:
                message = Messages.DEATH_BY_LAVA.asString();
                break;
        }
        // Replace placeholders
        if (attacker != null) {
            Team team = arena.getTeam(attacker);
            message = message.replace("{attacker}", attacker.getPlayer().getName())
                    .replace("{attacker_team_color}", team.getTeamColor().getChatColor().toString());
        }
        Team team = arena.getTeam(victim);
        message = message.replace("{player}", victim.getPlayer().getName())
                .replace("{player_team_color}", team.getTeamColor().getChatColor().toString());

        arena.broadcast(message);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        WoolPlayer player = PlayerManager.get(event.getPlayer());
        Arena arena = ArenaUtil.getArenaByPlayer(player);

        if (arena == null) return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            for (String material : Config.DISABLED_INTERACTION_BLOCKS.asStringList()) {
                if (Utils.matchMaterial(material, event.getClickedBlock().getType().toString())) {
                    event.setCancelled(true);
                    return;
                }
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

}
