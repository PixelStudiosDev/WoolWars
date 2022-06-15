package me.cubecrafter.woolwars.tasks;

import me.cubecrafter.woolwars.arena.GameArena;
import me.cubecrafter.woolwars.api.arena.GameState;
import me.cubecrafter.woolwars.powerup.PowerUp;

public class RoundOverTask extends ArenaTask {

    public RoundOverTask(GameArena arena) {
        super(arena);
    }

    @Override
    public void execute() {
    }

    @Override
    public void onEnd() {
        arena.setGameState(GameState.PRE_ROUND);
    }

    @Override
    public int getDuration() {
        return 5;
    }

    @Override
    public void onStart() {
        arena.getPowerUps().forEach(PowerUp::remove);
    }

}
