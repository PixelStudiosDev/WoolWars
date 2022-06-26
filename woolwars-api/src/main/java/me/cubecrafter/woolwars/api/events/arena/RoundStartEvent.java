package me.cubecrafter.woolwars.api.events.arena;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.api.arena.Arena;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class RoundStartEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Arena arena;
    private final int round;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
