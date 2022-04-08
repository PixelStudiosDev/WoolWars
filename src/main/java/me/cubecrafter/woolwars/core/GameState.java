package me.cubecrafter.woolwars.core;

public enum GameState {

    WAITING("Waiting"),
    PRE_ROUND("Pre Round"),
    STARTING("Starting"),
    PLAYING("Playing"),
    RESTARTING("Restarting");

    private final String name;

    GameState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
