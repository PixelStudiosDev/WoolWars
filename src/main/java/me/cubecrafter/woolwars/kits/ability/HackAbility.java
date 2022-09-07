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

package me.cubecrafter.woolwars.kits.ability;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.api.events.player.PlayerAbilityEvent;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HackAbility extends Ability {

    public HackAbility(ConfigurationSection section) {
        super(section);
    }

    @Override
    public void execute(Player player, Arena arena) {
        if (arena.isCenterLocked()) {
            TextUtil.sendMessage(player, section.getString("messages.center-already-locked"));
            return;
        }
        PlayerAbilityEvent event = new PlayerAbilityEvent(player, this, arena);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        TextUtil.sendMessage(player, Messages.ABILITY_USE.getAsString().replace("{name}", getDisplayName()));
        WoolWars.getInstance().getKitManager().addCooldown(player);
        arena.setCenterLocked(true);
        new BukkitRunnable() {
            double timer = section.getDouble("ability-duration");
            @Override
            public void run() {
                if (arena.getGameState() != GameState.ACTIVE_ROUND) {
                    arena.setCenterLocked(false);
                    cancel();
                    return;
                }
                if (timer <= 0) {
                    arena.setCenterLocked(false);
                    TextUtil.sendActionBar(arena.getPlayers(), section.getString("messages.center-unlocked"));
                    cancel();
                } else {
                    TextUtil.sendActionBar(arena.getPlayers(), section.getString("messages.center-locked").replace("{seconds}", String.format("%.1f", timer)));
                    timer -= 0.1;
                }
            }
        }.runTaskTimer(WoolWars.getInstance(), 2L, 2L);
    }

}
