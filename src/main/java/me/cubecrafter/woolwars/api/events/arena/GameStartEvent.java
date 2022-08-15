package me.cubecrafter.woolwars.api.events.arena;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.arena.Arena;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class GameStartEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Arena arena;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
