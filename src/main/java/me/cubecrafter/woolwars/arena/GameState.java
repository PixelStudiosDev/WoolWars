package me.cubecrafter.woolwars.arena;

public enum GameState {

    WAITING("Waiting"),
    PRE_ROUND("Pre Round"),
    STARTING("Starting"),
    PLAYING("Active Round"),
    ROUND_OVER("Round Over"),
    GAME_ENDED("Game Ended"),
    RESTARTING("Restarting");

    private final String name;

    GameState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
