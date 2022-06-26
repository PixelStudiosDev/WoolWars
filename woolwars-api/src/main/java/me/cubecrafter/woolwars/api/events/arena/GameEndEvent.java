package me.cubecrafter.woolwars.api.events.arena;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.api.arena.Arena;
import me.cubecrafter.woolwars.api.team.Team;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class GameEndEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Arena arena;
    private final Team winnerTeam;
    private final List<Team> loserTeams;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
