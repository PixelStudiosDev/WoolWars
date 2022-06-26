package me.cubecrafter.woolwars.api.events.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.api.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class PlayerKillEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player attacker;
    private final Player victim;
    private final KillCause cause;
    private final Arena arena;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public enum KillCause {

        PVP,
        VOID,
        FALL,
        LAVA,
        PROJECTILE,
        UNKNOWN;

    }

}
