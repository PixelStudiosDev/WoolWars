package me.cubecrafter.woolwars.core.tasks;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.Titles;
import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.core.Arena;
import me.cubecrafter.woolwars.core.GameState;
import me.cubecrafter.woolwars.core.Team;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class ArenaStartingTask implements Runnable {

    @Getter private final BukkitTask task;
    private final Arena arena;
    private int countdown = 5;

    public ArenaStartingTask(Arena arena) {
        this.arena = arena;
        task = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), this, 0L, 20L);
    }

    @Override
    public void run() {
        if (countdown == 0) {
            arena.assignTeams();
            arena.setGameState(GameState.PLAYING);
            for (Team team : arena.getTeams().values()) {
                team.setNameTags();
            }
            for (Player player : arena.getPlayers()) {
                Titles.sendTitle(player, 0, 20, 0, TextUtil.color("&c&lGame Started!"), "");
                XSound.play(player, "BLOCK_ANVIL_LAND");
            }
            task.cancel();
        } else {
            for (Player player : arena.getPlayers()) {
                Titles.sendTitle(player, 0, 20, 0, TextUtil.color("&a{time}".replace("{time}", String.valueOf(countdown))), "");
                XSound.play(player, "BLOCK_NOTE_BLOCK_BIT");
            }
        }
        countdown--;
    }

}
