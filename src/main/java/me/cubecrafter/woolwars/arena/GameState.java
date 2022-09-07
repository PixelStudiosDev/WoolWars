package me.cubecrafter.woolwars.arena;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.config.Messages;

@Getter
@RequiredArgsConstructor
public enum GameState {

    WAITING(Messages.GAME_STATE_WAITING.getAsString()),
    STARTING(Messages.GAME_STATE_STARTING.getAsString()),
    PRE_ROUND(Messages.GAME_STATE_PRE_ROUND.getAsString()),
    ACTIVE_ROUND(Messages.GAME_STATE_ACTIVE_ROUND.getAsString()),
    ROUND_OVER(Messages.GAME_STATE_ROUND_OVER.getAsString()),
    GAME_ENDED(Messages.GAME_STATE_GAME_ENDED.getAsString());

    private final String name;

}
