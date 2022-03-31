package me.cubecrafter.woolwars.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.core.Arena;
import me.cubecrafter.woolwars.core.GameState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class GameStateChangeEvent extends Event {

    private final Arena arena;
    private final GameState gameState;

    @Override
    public HandlerList getHandlers() {
        return null;
    }

}
