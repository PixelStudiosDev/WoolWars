package me.cubecrafter.woolwars.api.events.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.api.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class PlayerLeaveArenaEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Arena arena;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}

