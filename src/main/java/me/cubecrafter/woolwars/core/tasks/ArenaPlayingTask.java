package me.cubecrafter.woolwars.core.tasks;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.core.Arena;
import me.cubecrafter.woolwars.core.GameState;
import me.cubecrafter.woolwars.core.Team;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ArenaPlayingTask implements Runnable {

    @Getter private final BukkitTask task;
    private final Arena arena;
    private final Map<Team, Integer> placedBlocks = new HashMap<>();

    public ArenaPlayingTask(Arena arena) {
        this.arena = arena;
        arena.setTimer(60);
        task = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), this, 0L, 20L);
    }

    @Override
    public void run() {
        if (arena.getTimer() > 0) {
            arena.setTimer(arena.getTimer() - 1);
            for (Team team : placedBlocks.keySet()) {
                arena.sendMessage(team.getName() + ": " + placedBlocks.get(team));
            }
            arena.sendMessage("--------------------------------");
        } else if (arena.getTimer() == 0) {
            arena.sendMessage(getBestTeam() != null ? getBestTeam().getName() : "Best Team is null");
            task.cancel();
        }
    }

    public void addPlacedBlock(Team team) {
        placedBlocks.merge(team, 1, Integer::sum);
    }

    public void removePlacedBlock(Team team) {
        placedBlocks.put(team, placedBlocks.get(team) - 1);
    }

    public Team getBestTeam() {
        return placedBlocks.keySet().stream().max(Comparator.comparing(placedBlocks::get)).orElse(null);
    }

    public void checkWinners() {
        for (Map.Entry<Team, Integer> entry : placedBlocks.entrySet()) {
            if (entry.getValue() != arena.getBlocksRegion().getTotalBlocks()) continue;
            Team team = entry.getKey();
            team.addPoint();
            placedBlocks.clear();
            arena.getBlocksRegion().clear();
            arena.sendTitle(40, "{teamcolor}{teamname}".replace("{teamcolor}", team.getTeamColor().getChatColor().toString()).replace("{teamname}", team.getName()), "&e&lWINNER");
            arena.playSound("ENTITY_PLAYER_LEVELUP");
            if (team.getPoints() == arena.getRequiredPoints()) {
                arena.sendTitle(40, team.getName(), "&e&lWINNER TEAM");
                arena.restart();
                task.cancel();
                return;
            }
            arena.setGameState(GameState.PRE_ROUND);
            task.cancel();
        }
    }

}
