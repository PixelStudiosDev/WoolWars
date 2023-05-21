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

package me.cubecrafter.woolwars.kit.ability;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

public class HackAbility extends Ability {

    public HackAbility(ConfigurationSection section) {
        super(section, Type.HACK);
    }

    @Override
    public boolean handle(WoolPlayer player, Arena arena) {
        if (arena.isCenterLocked()) {
            player.send(section.getString("messages.center-already-locked"));
            return false;
        }
        arena.setCenterLocked(true);

        new BukkitRunnable() {
            double timer = section.getDouble("duration");

            @Override
            public void run() {
                if (arena.getState() != GameState.ACTIVE_ROUND) {
                    arena.setCenterLocked(false);
                    cancel();
                    return;
                }
                if (timer <= 0) {
                    arena.setCenterLocked(false);
                    arena.broadcastActionBar(section.getString("messages.center-unlocked"));
                    cancel();
                } else {
                    arena.broadcastActionBar(section.getString("messages.center-locked").replace("{seconds}", String.format("%.1f", timer)));
                    timer -= 0.1;
                }
            }
        }.runTaskTimer(WoolWars.get(), 2L, 2L);
        return true;
    }

}
