package me.cubecrafter.woolwars.api.events.arena;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class GameStateChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Arena arena;
    private final GameState oldState;
    private final GameState newState;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
