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

import me.cubecrafter.woolwars.api.events.player.PlayerKillEvent;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.config.Abilities;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.team.Team;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL) && Configuration.DISABLE_FALL_DAMAGE.getAsBoolean()) {
            e.setCancelled(true);
            return;
        }
        if (arena.getGameState() != GameState.ACTIVE_ROUND || arena.getDeadPlayers().contains(player)) {
            if (e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
                player.setFireTicks(0);
            }
            e.setCancelled(true);
            return;
        }
        if (e instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent) e;
            if (damageByEntityEvent.getDamager() instanceof TNTPrimed) {
                TNTPrimed tnt = (TNTPrimed) damageByEntityEvent.getDamager();
                if (tnt.hasMetadata("woolwars")) {
                    player.setVelocity(player.getLocation().toVector().subtract(tnt.getLocation().toVector()).normalize().multiply(Abilities.KNOCKBACK_TNT.getAsSection().getDouble("knockback-power")));
                    e.setCancelled(true);
                    return;
                }
            } else if (damageByEntityEvent.getDamager() instanceof Player) {
                Player damager = (Player) damageByEntityEvent.getDamager();
                if (arena.getDeadPlayers().contains(damager) || arena.isTeammate(player, damager)) {
                    e.setCancelled(true);
                    return;
                }
            } else if (damageByEntityEvent.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) damageByEntityEvent.getDamager();
                if (projectile.getShooter() instanceof Player) {
                    Player damager = (Player) projectile.getShooter();
                    if (arena.isTeammate(player, damager)) {
                        e.setCancelled(true);
                        return;
                    }
                }
            }
        }
        if (((player.getHealth() - e.getFinalDamage()) <= 0)) {
            e.setCancelled(true);
            Team playerTeam = arena.getTeamByPlayer(player);
            if (e instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
                if (event.getDamager() instanceof Player) {
                    Player damager = (Player) event.getDamager();
                    PlayerKillEvent killEvent = new PlayerKillEvent(damager, player, PlayerKillEvent.KillCause.PVP, arena);
                    Bukkit.getServer().getPluginManager().callEvent(killEvent);
                    Team damagerTeam = arena.getTeamByPlayer(damager);
                    TextUtil.sendMessage(arena.getPlayers(), Messages.KILL_MESSAGE.getAsString()
                            .replace("{player}", player.getName())
                            .replace("{player_team_color}", playerTeam.getTeamColor().getChatColor().toString())
                            .replace("{attacker}", damager.getName())
                            .replace("{attacker_team_color}", damagerTeam.getTeamColor().getChatColor().toString()));
                    arena.getRoundTask().addKill(damager);
                } else if (event.getDamager() instanceof Projectile) {
                    PlayerKillEvent killEvent = new PlayerKillEvent(null, player, PlayerKillEvent.KillCause.PROJECTILE, arena);
                    Bukkit.getServer().getPluginManager().callEvent(killEvent);
                    Projectile projectile = (Projectile) event.getDamager();
                    if (projectile.getShooter() instanceof Player) {
                        Player damager = (Player) projectile.getShooter();
                        Team damagerTeam = arena.getTeamByPlayer(damager);
                        TextUtil.sendMessage(arena.getPlayers(), Messages.KILL_MESSAGE.getAsString()
                                .replace("{player}", player.getName())
                                .replace("{player_team_color}", playerTeam.getTeamColor().getChatColor().toString())
                                .replace("{attacker}", damager.getName())
                                .replace("{attacker_team_color}", damagerTeam.getTeamColor().getChatColor().toString()));
                        arena.getRoundTask().addKill(damager);
                    }
                }
            } else if (e.getCause().equals(EntityDamageEvent.DamageCause.LAVA)) {
                PlayerKillEvent killEvent = new PlayerKillEvent(null, player, PlayerKillEvent.KillCause.LAVA, arena);
                Bukkit.getServer().getPluginManager().callEvent(killEvent);
                TextUtil.sendMessage(arena.getPlayers(), Messages.DEATH_BY_LAVA.getAsString()
                        .replace("{player}", player.getName())
                        .replace("{player_team_color}", playerTeam.getTeamColor().getChatColor().toString()));
            } else if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                PlayerKillEvent killEvent = new PlayerKillEvent(null, player, PlayerKillEvent.KillCause.FALL, arena);
                Bukkit.getServer().getPluginManager().callEvent(killEvent);
                TextUtil.sendMessage(arena.getPlayers(), Messages.DEATH_BY_FALL.getAsString()
                        .replace("{player}", player.getName())
                        .replace("{player_team_color}", playerTeam.getTeamColor().getChatColor().toString()));
            } else if (e.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                PlayerKillEvent killEvent = new PlayerKillEvent(null, player, PlayerKillEvent.KillCause.VOID, arena);
                Bukkit.getServer().getPluginManager().callEvent(killEvent);
                TextUtil.sendMessage(arena.getPlayers(), Messages.DEATH_BY_VOID.getAsString()
                        .replace("{player}", player.getName())
                        .replace("{player_team_color}", playerTeam.getTeamColor().getChatColor().toString()));
            } else {
                PlayerKillEvent killEvent = new PlayerKillEvent(null, player, PlayerKillEvent.KillCause.UNKNOWN, arena);
                Bukkit.getServer().getPluginManager().callEvent(killEvent);
                TextUtil.sendMessage(arena.getPlayers(), Messages.DEATH_GENERIC.getAsString()
                        .replace("{player}", player.getName())
                        .replace("{player_team_color}", playerTeam.getTeamColor().getChatColor().toString()));
            }
            ArenaUtil.handleDeath(player, arena);
        }
    }

}
