package me.cubecrafter.woolwars.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStateChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public GameStateChangeEvent() {

    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
