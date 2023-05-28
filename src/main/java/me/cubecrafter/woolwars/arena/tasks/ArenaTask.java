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

import lombok.Getter;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.xutils.Tasks;
import org.bukkit.scheduler.BukkitTask;

@Getter
public abstract class ArenaTask {

    protected final Arena arena;

    private final BukkitTask task;
    private final int duration;

    public ArenaTask(Arena arena, int duration) {
        this.arena = arena;
        this.duration = duration;

        arena.setTimer(duration);
        task = Tasks.repeat(() -> {
            if (arena.getTimer() == 0) {
                GameState state = end();
                if (state != null) {
                    arena.setState(state);
                } else {
                    cancel();
                }
            } else {
                execute();
                arena.setTimer(arena.getTimer() - 1);
            }
        }, 20L, 20L);
        start();
    }

    public void start() {}
    public void execute() {}

    public GameState end() {
        return GameState.WAITING;
    }

    public int getSecondsElapsed() {
        return duration - arena.getTimer();
    }

    public void cancel() {
        task.cancel();
    }

}
