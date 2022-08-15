package me.cubecrafter.woolwars.api.events.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.kits.Ability;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class PlayerUseAbilityEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @Setter
    private boolean cancelled;

    private final Player player;
    private final Ability ability;
    private final Arena arena;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
