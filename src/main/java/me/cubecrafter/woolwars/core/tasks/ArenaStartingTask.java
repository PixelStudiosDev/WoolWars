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

    public ArenaStartingTask(Arena arena) {
        this.arena = arena;
        arena.setTimer(5);
        task = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), this, 0L, 20L);
    }

    @Override
    public void run() {
        if (arena.getTimer() == 0) {
            arena.setGameState(GameState.PRE_ROUND);
            arena.assignTeams();
            for (Team team : arena.getTeams()) {
                team.setNameTags();
                team.applyArmor();
                team.teleportToSpawn();
            }
            for (Player player : arena.getPlayers()) {
                Titles.sendTitle(player, 0, 40, 0, TextUtil.color("&e&lPRE ROUND"), TextUtil.color("&bSelect your kit!"));
                XSound.play(player, "BLOCK_ANVIL_LAND");
            }
            task.cancel();
        } else {
            arena.broadcast(TextUtil.color("&eThe game starts in &c{seconds} &eseconds!".replace("{seconds}", String.valueOf(arena.getTimer()))));
            for (Player player : arena.getPlayers()) {
                XSound.play(player, "ENTITY_CHICKEN_EGG");
            }
            arena.setTimer(arena.getTimer() - 1);
        }
    }

}
