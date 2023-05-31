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

package me.cubecrafter.woolwars.arena.tasks;

import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.xutils.ReflectionUtil;
import me.cubecrafter.xutils.Tasks;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

@SuppressWarnings("deprecation")
public class ParticleTask implements Runnable {

    private static final boolean LEGACY = ReflectionUtil.VERSION < 9;

    private final BukkitTask task;
    private final Arena arena;

    public ParticleTask(Arena arena) {
        this.arena = arena;
        this.task = Tasks.repeat(this, 20, 20);
    }

    @Override
    public void run() {
        World world = arena.getWorld();

        for (Location location : arena.getJumpPads()) {
            for (int i = 0; i <= 1; i++) {
                for (int j = 0; j <= 1; j++) {
                    Location loc = location.clone().add(i, 1.25, j);
                    if (LEGACY) {
                        world.playEffect(loc, Effect.HAPPY_VILLAGER, 0);
                    } else {
                        world.spawnParticle(Particle.VILLAGER_HAPPY, loc, 1);
                    }
                }
            }
        }
    }

    public void cancel() {
        task.cancel();
    }

}
