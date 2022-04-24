package me.cubecrafter.woolwars.arena.tasks;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class ArenaGameEndedTask implements Runnable {

    @Getter private final BukkitTask task;
    private final Arena arena;

    public ArenaGameEndedTask(Arena arena) {
        this.arena = arena;
        arena.setTimer(10);
        task = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), this, 0L, 20L);
        TextUtil.info("GAME ENDED TASK - GAME ENDED TASK - GAME ENDED TASK - GAME ENDED TASK");
    }

    @Override
    public void run() {
        if (arena.getTimer() > 0) {
            arena.setTimer(arena.getTimer() - 1);
        } else if (arena.getTimer() == 0) {
            arena.setGameState(GameState.RESTARTING);
            task.cancel();
        }
    }


}
