package me.cubecrafter.woolwars.database;

import lombok.Data;

import java.util.UUID;

@Data
public class PlayerData {

    private final UUID uuid;
    private int wins;
    private int losses;
    private int gamesPlayed;
    private int kills;
    private int deaths;
    private int woolPlaced;
    private int blocksBroken;
    private int powerUpsCollected;
    private String selectedKit;

}
