package me.cubecrafter.woolwars.tasks;

import me.cubecrafter.woolwars.arena.GameArena;
import me.cubecrafter.woolwars.api.arena.GameState;
import me.cubecrafter.woolwars.powerup.PowerUp;

public class RoundOverTask extends ArenaTask {

    public RoundOverTask(GameArena arena, int duration) {
        super(arena, duration);
        arena.getPowerUps().forEach(PowerUp::remove);
    }

    @Override
    public void onEnd() {
        arena.setGameState(GameState.PRE_ROUND);
    }

}
