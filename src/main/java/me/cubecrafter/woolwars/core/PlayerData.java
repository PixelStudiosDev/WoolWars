package me.cubecrafter.woolwars.core;

import lombok.Data;

import java.util.UUID;

@Data
public class PlayerData {

    private final UUID uuid;
    private String kit;
    // other things like player stats...

}
