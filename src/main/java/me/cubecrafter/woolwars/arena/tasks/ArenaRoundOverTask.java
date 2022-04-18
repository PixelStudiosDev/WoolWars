package me.cubecrafter.woolwars.arena.tasks;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.arena.PowerUp;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class ArenaRoundOverTask implements Runnable {

    private final Arena arena;
    @Getter private final BukkitTask task;

    public ArenaRoundOverTask(Arena arena) {
        this.arena = arena;
        arena.setTimer(5);
        arena.getPowerUps().forEach(PowerUp::remove);
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
