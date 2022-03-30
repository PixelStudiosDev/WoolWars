package me.cubecrafter.woolwars.core.tasks;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.Titles;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.core.Arena;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class ArenaStartingTask implements Runnable {

    private final BukkitTask task;
    private final Arena arena;
    private int countdown = 5;

    public ArenaStartingTask(Arena arena) {
        this.arena = arena;
        task = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), this, 0L, 20L);
    }

    @Override
    public void run() {
        if (countdown == 0) {
            for (Player player : arena.getPlayers()) {
                Titles.sendTitle(player, 0, 20, 0, "Game Started!", "");
                XSound.play(player, "BLOCK_ANVIL_LAND");
            }
            task.cancel();
        }
        for (Player player : arena.getPlayers()) {
            Titles.sendTitle(player, 0, 20, 0, String.valueOf(countdown), "");
            XSound.play(player, "BLOCK_NOTE_BLOCK_BIT");
        }
        countdown--;
    }

    public BukkitTask getTask() {
        return task;
    }

}
