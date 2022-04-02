package me.cubecrafter.woolwars.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private final Map<UUID, PlayerData> playerData = new HashMap<>();

    public PlayerData getPlayerData(UUID uuid) {
        return playerData.get(uuid);
    }

}
