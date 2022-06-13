package me.cubecrafter.woolwars.api.database;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class PlayerData {

    private final UUID uuid;
    private int wins;
    private int losses;
    private int gamesPlayed;
    private int kills;
    private int deaths;
    private int placedWool;
    private int brokenBlocks;
    private int powerUpsCollected;
    private String selectedKit;

}
