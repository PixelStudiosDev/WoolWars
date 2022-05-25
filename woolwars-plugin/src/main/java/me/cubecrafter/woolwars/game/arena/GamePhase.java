package me.cubecrafter.woolwars.game.arena;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum GamePhase {

    NONE("None"),
    PRE_ROUND("Pre Round"),
    ACTIVE_ROUND("Active Round"),
    ROUND_OVER("Round Over"),
    GAME_ENDED("Game Ended");

    @Getter private final String name;

}
