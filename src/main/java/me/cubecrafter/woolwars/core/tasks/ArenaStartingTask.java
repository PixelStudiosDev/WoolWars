package me.cubecrafter.woolwars.core.tasks;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.core.Arena;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class ArenaStartingTask implements Runnable {

    private final BukkitTask task;

    public ArenaStartingTask(Arena arena) {
        task = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), this, 0L, 20L);
    }

    @Override
    public void run() {

    }

}
