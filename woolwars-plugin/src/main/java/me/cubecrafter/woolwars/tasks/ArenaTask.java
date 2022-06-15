package me.cubecrafter.woolwars.tasks;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.GameArena;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public abstract class ArenaTask {

    protected final GameArena arena;
    private final BukkitTask task;

    public ArenaTask(GameArena arena) {
        this.arena = arena;
        arena.setTimer(getDuration());
        onStart();
        task = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), () -> {
            if (arena.getTimer() == 0) {
                onEnd();
                cancel();
            } else {
                execute();
                arena.setTimer(arena.getTimer() - 1);
            }
        }, 0, 20L);
    }

    public void cancel() {
        task.cancel();
    }

    public abstract void onStart();
    public abstract void execute();
    public abstract void onEnd();
    public abstract int getDuration();

}
