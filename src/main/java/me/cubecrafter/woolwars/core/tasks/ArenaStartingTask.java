package me.cubecrafter.woolwars.core.tasks;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.core.Arena;
import me.cubecrafter.woolwars.core.GameState;
import me.cubecrafter.woolwars.core.Team;
import org.bukkit.Bukkit;
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
            arena.sendTitle(40, "&e&lPRE ROUND", "&bSelect your kit!");
            arena.playSound("BLOCK_ANVIL_LAND");
            task.cancel();
        } else {
            arena.sendMessage("&eThe game starts in &c{seconds} &eseconds!".replace("{seconds}", String.valueOf(arena.getTimer())));
            arena.playSound("ENTITY_CHICKEN_EGG");
            arena.setTimer(arena.getTimer() - 1);
        }
    }

}
