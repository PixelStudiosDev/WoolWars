package me.cubecrafter.woolwars.core.tasks;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.core.Arena;
import me.cubecrafter.woolwars.core.GameState;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class ArenaRoundOverTask implements Runnable {

    private final Arena arena;
    @Getter private final BukkitTask task;

    public ArenaRoundOverTask(Arena arena) {
        this.arena = arena;
        arena.setTimer(5);
        task = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), this, 0L, 20L);
    }

    @Override
    public void run() {

        if (arena.getTimer() > 0) {
            arena.setTimer(arena.getTimer() - 1);
        } else if (arena.getTimer() == 0) {
            arena.setGameState(GameState.PRE_ROUND);
            task.cancel();
        }

    }

}
