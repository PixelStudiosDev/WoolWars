package me.cubecrafter.woolwars.tasks;

import me.cubecrafter.woolwars.api.arena.GameState;
import me.cubecrafter.woolwars.arena.GameArena;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.team.GameTeam;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.TextUtil;

public class StartingTask extends ArenaTask {

    public StartingTask(GameArena arena) {
        super(arena);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void execute() {
        if (arena.getTimer() % 5 == 0 || arena.getTimer() <= 3) {
            TextUtil.sendMessage(arena.getPlayers(), Messages.GAME_START_COUNTDOWN.getAsString().replace("{seconds}", String.valueOf(arena.getTimer())));
            ArenaUtil.playSound(arena.getPlayers(), Configuration.SOUNDS_COUNTDOWN.getAsString());
        }
    }

    @Override
    public void onEnd() {
        Messages.GAME_START_MESSAGE.send(arena.getPlayers());
        arena.assignTeams();
        arena.getTeams().forEach(GameTeam::applyNameTags);
        arena.setGameState(GameState.PRE_ROUND);
    }

    @Override
    public int getDuration() {
        return Configuration.STARTING_COUNTDOWN.getAsInt();
    }

}
