/*
 * Copyright (c) 2019 Jonah Seguin.  All rights reserved.  You may not modify, decompile, distribute or use any code/text contained in this document(plugin) without explicit signed permission from Jonah Seguin.
 */

package me.cubecrafter.woolwars.utils.scoreboard.view;

import lombok.Getter;
import me.cubecrafter.woolwars.utils.scoreboard.Absorb;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;

@Getter
public class ViewUpdater implements Runnable {

    private final Plugin plugin;
    private final Set<Absorb> boards = new HashSet<>();

    private long ticks;
    private BukkitTask task = null;
    private boolean running = false;

    public ViewUpdater(Plugin plugin, long ticks) {
        this.plugin = plugin;
        this.ticks = ticks;
    }

    public void start() {
        if (!this.running) {
            this.running = true;
            this.task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this, 0L, ticks);
        }
    }

    public void stop() {
        if (this.running) {
            this.running = false;
            if (this.task != null) {
                this.task.cancel();
                this.task = null;
            }
        }
    }

    public void setTicks(long ticks) {
        this.ticks = ticks;
        if (this.isRunning()) {
            this.stop();
            this.start();
        }
    }

    public synchronized void registerBoard(Absorb absorb) {
        this.boards.add(absorb);
    }

    public synchronized void unregisterBoard(Absorb absorb) {
        this.boards.remove(absorb);
    }

    @Override
    public void run() {
        synchronized (this.boards) {
            this.boards.forEach(board -> {
                board.view().render();
            });
        }
    }
}
