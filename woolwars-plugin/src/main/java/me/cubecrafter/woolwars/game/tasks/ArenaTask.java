package me.cubecrafter.woolwars.game.tasks;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.game.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public abstract class ArenaTask {

    protected final Arena arena;
    private final BukkitTask task;

    public ArenaTask(Arena arena, int duration) {
        this.arena = arena;
        arena.setTimer(duration);
        task = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), () -> {
            if (arena.getTimer() == 0) {
                onEnd();
                cancelTask();
            } else if (arena.getTimer() > 0) {
                execute();
                arena.setTimer(arena.getTimer() - 1);
            }
        }, 0L, 20L);
    }

    public void cancelTask() {
        if (task != null) task.cancel();
    }

    public void execute() {}

    public void onEnd() {}

}
