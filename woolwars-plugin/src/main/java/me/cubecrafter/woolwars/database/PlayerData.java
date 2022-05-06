package me.cubecrafter.woolwars.database;

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
    private int placedBlocks;
    private int brokenBlocks;
    private String selectedKit;

}
