package me.cubecrafter.woolwars.tasks;

import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.powerup.PowerUp;

public class RoundOverTask extends ArenaTask {

    public RoundOverTask(Arena arena) {
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
        return Configuration.ROUND_OVER_DURATION.getAsInt();
    }

    @Override
    public void onStart() {
        arena.getPowerUps().forEach(PowerUp::remove);
    }

}
