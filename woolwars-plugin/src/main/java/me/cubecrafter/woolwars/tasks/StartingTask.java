package me.cubecrafter.woolwars.tasks;

import me.cubecrafter.woolwars.api.arena.GameState;
import me.cubecrafter.woolwars.arena.GameArena;
import me.cubecrafter.woolwars.team.GameTeam;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.TextUtil;

public class StartingTask extends ArenaTask {

    public StartingTask(GameArena arena, int duration) {
        super(arena, duration);
    }

    @Override
    public void execute() {
        if (arena.getTimer() % 5 == 0 || arena.getTimer() <= 3) {
            TextUtil.sendMessage(arena.getPlayers(), "&7The game starts in &a{seconds} &7seconds!".replace("{seconds}", String.valueOf(arena.getTimer())));
            ArenaUtil.playSound(arena.getPlayers(), "ENTITY_CHICKEN_EGG");
        }
    }

    @Override
    public void onEnd() {
        arena.assignTeams();
        arena.getTeams().forEach(GameTeam::setNameTags);
        TextUtil.sendTitle(arena.getPlayers(), 2, "&e&lPRE ROUND", "&7Select your kit!");
        TextUtil.sendMessage(arena.getPlayers(), "&8&m--------------------------------------------------            ");
        TextUtil.sendMessage(arena.getPlayers(), "&c               &lWOOL WARS                                      ");
        TextUtil.sendMessage(arena.getPlayers(), "&7Matches are best of &e" + arena.getMaxRounds());
        TextUtil.sendMessage(arena.getPlayers(), "&7Place your team's color wool in the &acenter &7to win the round!");
        TextUtil.sendMessage(arena.getPlayers(), "&8&m--------------------------------------------------            ");
        arena.setGameState(GameState.PRE_ROUND);
    }

}
