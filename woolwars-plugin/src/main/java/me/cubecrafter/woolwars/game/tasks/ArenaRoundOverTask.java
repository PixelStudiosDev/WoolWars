package me.cubecrafter.woolwars.game.tasks;

import me.cubecrafter.woolwars.game.arena.Arena;
import me.cubecrafter.woolwars.game.arena.GamePhase;
import me.cubecrafter.woolwars.game.powerup.PowerUp;

public class ArenaRoundOverTask extends ArenaTask {

    public ArenaRoundOverTask(Arena arena, int duration) {
        super(arena, duration);
        arena.getPowerUps().forEach(PowerUp::remove);
    }

    @Override
    public void onTimerEnd() {
        arena.setGamePhase(GamePhase.PRE_ROUND);
    }

}
