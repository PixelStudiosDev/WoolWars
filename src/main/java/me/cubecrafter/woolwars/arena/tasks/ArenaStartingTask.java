package me.cubecrafter.woolwars.arena.tasks;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.arena.Team;
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
                team.teleportToSpawn();
            }
            arena.sendTitle(40, "&e&lPRE ROUND", "&7Select your kit!");
            task.cancel();
        } else {
            arena.sendMessage("&7The game starts in &a{seconds} &7seconds!".replace("{seconds}", String.valueOf(arena.getTimer())));
            arena.playSound("ENTITY_CHICKEN_EGG");
            arena.setTimer(arena.getTimer() - 1);
        }
    }

}
