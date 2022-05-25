package me.cubecrafter.woolwars.database;

import lombok.Getter;
import lombok.Setter;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {

    @Getter private final UUID uuid;
    @Getter @Setter private String selectedKit;

    private final Map<StatisticType, Integer> stats = new EnumMap<>(StatisticType.class);

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        for (StatisticType type : StatisticType.values()) {
            stats.put(type, 0);
        }
    }

    public int getStatistic(StatisticType type) {
        return stats.get(type);
    }

    public void setStatistic(StatisticType type, int value) {
        stats.put(type, value);
    }

}
