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

import com.cryptomorin.xseries.messages.ActionBar;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.api.events.player.PlayerKillEvent;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.ArenaUtil;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.storage.player.PlayerManager;
import me.cubecrafter.woolwars.storage.player.StatisticType;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.VersionUtil;
import me.cubecrafter.xutils.Events;
import me.cubecrafter.xutils.TextUtil;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class DamageListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        WoolPlayer woolPlayer = PlayerManager.get(player);
        Arena arena = ArenaUtil.getArenaByPlayer(woolPlayer);

        if (arena == null) return;

        if (arena.getState() != GameState.ACTIVE_ROUND || !woolPlayer.isAlive()) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
                player.setFireTicks(0);
            }
            event.setCancelled(true);
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL && Config.DISABLE_FALL_DAMAGE.asBoolean()) {
            event.setCancelled(true);
            return;
        }

        if (event instanceof EntityDamageByEntityEvent) {
            onDamageByEntity((EntityDamageByEntityEvent) event, woolPlayer, arena);
            return;
        }

        if (((player.getHealth() - event.getFinalDamage()) <= 0)) {
            event.setCancelled(true);

            switch (event.getCause()) {
                case LAVA:
                    Events.call(new PlayerKillEvent(arena, null, woolPlayer, PlayerKillEvent.KillCause.LAVA));
                    break;
                case FALL:
                    Events.call(new PlayerKillEvent(arena, null, woolPlayer, PlayerKillEvent.KillCause.FALL));
                    break;
                case VOID:
                    Events.call(new PlayerKillEvent(arena, null, woolPlayer, PlayerKillEvent.KillCause.VOID));
                    break;
                default:
                    Events.call(new PlayerKillEvent(arena, null, woolPlayer, PlayerKillEvent.KillCause.UNKNOWN));
                    break;
            }
            // Handle fake death
            handleDeath(woolPlayer, arena);
        }
    }

    public void onDamageByEntity(EntityDamageByEntityEvent event, WoolPlayer player, Arena arena) {
        Entity damager = event.getDamager();

        switch (event.getCause()) {
            case ENTITY_EXPLOSION:
                if (damager.hasMetadata("woolwars")) {
                    Vector velocity = player.getLocation().toVector().subtract(damager.getLocation().toVector()).normalize();
                    velocity.multiply(damager.getMetadata("woolwars").get(0).asDouble());
                    player.getPlayer().setVelocity(velocity);
                    event.setCancelled(true);
                }
                break;
            case PROJECTILE:
                Projectile projectile = (Projectile) damager;
                if (projectile.getShooter() instanceof Player) {
                    WoolPlayer shooter = PlayerManager.get((Player) projectile.getShooter());
                    if (arena.isTeammate(player, shooter)) {
                        event.setCancelled(true);
                    }
                }
                break;
            case ENTITY_ATTACK:
                if (damager instanceof Player) {
                    WoolPlayer attacker = PlayerManager.get((Player) damager);
                    if (arena.isTeammate(player, attacker)) {
                        event.setCancelled(true);
                    }
                }
                break;
        }

        if ((player.getPlayer().getHealth() - event.getFinalDamage()) <= 0) {
            event.setCancelled(true);

            if (damager instanceof Player) {
                WoolPlayer attacker = PlayerManager.get((Player) damager);
                Events.call(new PlayerKillEvent(arena, attacker, player, PlayerKillEvent.KillCause.PVP));

            } else if (damager instanceof Projectile) {
                Projectile projectile = (Projectile) damager;
                if (projectile.getShooter() instanceof Player) {
                    WoolPlayer shooter = PlayerManager.get((Player) projectile.getShooter());
                    Events.call(new PlayerKillEvent(arena, shooter, player, PlayerKillEvent.KillCause.PROJECTILE));
                } else {
                    Events.call(new PlayerKillEvent(arena, null, player, PlayerKillEvent.KillCause.PROJECTILE));
                }
            }
            // Handle fake death
            handleDeath(player, arena);
        }
    }

    public static void handleDeath(WoolPlayer player, Arena arena) {
        arena.playSound(Config.SOUNDS_PLAYER_DEATH.asString());

        player.getData().addRoundStatistic(StatisticType.DEATHS, 1);
        player.setAlive(false);

        player.getPlayer().setGameMode(GameMode.ADVENTURE);
        player.getPlayer().setAllowFlight(true);
        player.getPlayer().setFlying(true);
        player.getPlayer().getInventory().setArmorContents(null);
        player.getPlayer().getInventory().clear();
        player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
        player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
        player.getPlayer().setFireTicks(0);
        player.getPlayer().setHealth(20);
        player.sendTitle(Messages.DEATH_TITLE.asString(), Messages.DEATH_SUBTITLE.asString(), 2);

        for (WoolPlayer alive : arena.getAlivePlayers()) {
            VersionUtil.hidePlayer(alive.getPlayer(), player.getPlayer());
        }
        for (WoolPlayer dead : arena.getDeadPlayers()) {
            VersionUtil.showPlayer(player.getPlayer(), dead.getPlayer());
        }

        ItemStack teleporter = ItemBuilder.fromConfig(Config.TELEPORTER_ITEM.asSection()).setTag("teleporter").build();
        player.getPlayer().getInventory().setItem(Config.TELEPORTER_ITEM.asSection().getInt("slot"), teleporter);
        player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0, false, false));

        player.send(Messages.DEATH_RESPAWN_NEXT_ROUND.asString());
        ActionBar.sendActionBarWhile(WoolWars.get(), player.getPlayer(), TextUtil.color(Messages.DEATH_RESPAWN_NEXT_ROUND.asString()), () -> !player.isAlive());

        if (arena.getAlivePlayers().isEmpty()) {
            arena.broadcast(Messages.ALL_PLAYERS_DEAD.asString());
            arena.setState(arena.getCurrentTask().end());
        }
    }

}
