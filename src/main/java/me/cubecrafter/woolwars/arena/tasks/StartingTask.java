/*
 * Wool Wars
 * Copyright (C) 2023 CubeCrafter Development
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.cubecrafter.woolwars.arena.tasks;

import me.cubecrafter.woolwars.api.events.arena.GameStartEvent;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.arena.team.Team;
import me.cubecrafter.xutils.Events;

public class StartingTask extends ArenaTask {

    public StartingTask(Arena arena) {
        super(arena, Config.STARTING_COUNTDOWN.asInt());
    }

    @Override
    public void execute() {
        if (arena.getTimer() % 10 == 0 || arena.getTimer() <= 5) {
            arena.broadcast(Messages.GAME_START_COUNTDOWN.asString().replace("{seconds}", String.valueOf(arena.getTimer())));
            arena.playSound(Config.SOUNDS_COUNTDOWN.asString());
        }
    }

    @Override
    public GameState end() {
        Events.call(new GameStartEvent(arena));
        arena.assignTeams();
        arena.getTeams().forEach(Team::updateNameTags);
        // Start the game
        return GameState.PRE_ROUND;
    }

}
