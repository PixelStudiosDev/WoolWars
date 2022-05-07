package me.cubecrafter.woolwars.game.tasks;

import me.cubecrafter.woolwars.game.arena.Arena;
import me.cubecrafter.woolwars.game.arena.GameState;
import me.cubecrafter.woolwars.game.team.Team;

public class ArenaStartingTask extends ArenaTask {

    public ArenaStartingTask(Arena arena) {
        super(arena);
    }

    @Override
    public void execute() {
        if (arena.getTimer() % 5 == 0 || arena.getTimer() <= 3) {
            arena.sendMessage("&7The game starts in &a{seconds} &7seconds!".replace("{seconds}", String.valueOf(arena.getTimer())));
            arena.playSound("ENTITY_CHICKEN_EGG");
        }
    }

    @Override
    public void onTimerEnd() {
        arena.assignTeams();
        for (Team team : arena.getTeams()) {
            team.setNameTags();
            team.teleportToSpawn();
        }
        arena.sendTitle(40, "&e&lPRE ROUND", "&7Select your kit!");
        arena.setGameState(GameState.PRE_ROUND);
        arena.sendMessage("&8&m&l---------------------------------------------------------");
        arena.sendMessage("&c               &lWOOL WARS");
        arena.sendMessage("&7Matches are best of &e" + arena.getMaxRounds());
        arena.sendMessage("&7Place your team's color wool in the &acenter &7to win the round!");
        arena.sendMessage("&8&m---------------------------------------------------------");
    }

    @Override
    public int getTaskDuration() {
        return 10;
    }

}
