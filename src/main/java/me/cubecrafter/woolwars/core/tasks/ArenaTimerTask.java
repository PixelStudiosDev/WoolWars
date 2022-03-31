package me.cubecrafter.woolwars.core.tasks;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.core.Arena;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class ArenaTimerTask implements Runnable {

    @Getter private final BukkitTask task;
    private final Arena arena;
    private int timer = 120;

    public ArenaTimerTask(Arena arena) {
        this.arena = arena;
        task = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), this, 0L, 20L);
    }

    @Override
    public void run() {
        timer--;
        arena.broadcast(getTimerFormatted());
    }

    public String getTimerFormatted() {
        int minutes = (timer / 60) % 60;
        return (minutes > 0 ? minutes + ":" : "") + timer;
    }

}
