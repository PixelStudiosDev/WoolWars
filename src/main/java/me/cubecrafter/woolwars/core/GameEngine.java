package me.cubecrafter.woolwars.core;

import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.entity.Player;

public class GameEngine {

    private final Arena arena;

    public GameEngine(Arena arena) {
        this.arena = arena;
        for (Player player : arena.getPlayers()) {
            player.teleport(arena.getSpawnLocation());
            player.sendMessage(TextUtil.color("&cGame Started!"));
        }
    }

}
