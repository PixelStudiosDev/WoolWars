package me.cubecrafter.woolwars.game.tasks;

import me.cubecrafter.woolwars.game.arena.Arena;
import me.cubecrafter.woolwars.game.GameState;
import me.cubecrafter.woolwars.game.team.Team;

public class ArenaStartingTask extends ArenaTask {

    public ArenaStartingTask(Arena arena) {
        super(arena);
        arena.setTimer(10);
    }

    @Override
    public void execute() {
        arena.sendMessage("&7The game starts in &a{seconds} &7seconds!".replace("{seconds}", String.valueOf(arena.getTimer())));
        arena.playSound("ENTITY_CHICKEN_EGG");
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
    }

}
