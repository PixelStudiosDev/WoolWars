package me.cubecrafter.woolwars.arena.tasks;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.arena.PowerUp;
import me.cubecrafter.woolwars.arena.Team;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ArenaPlayingTask implements Runnable {

    @Getter private final BukkitTask task;
    @Getter private final BukkitTask rotatePowerUpsTask;
    private final Arena arena;
    private final Map<Team, Integer> placedBlocks = new HashMap<>();

    public ArenaPlayingTask(Arena arena) {
        this.arena = arena;
        arena.setTimer(60);
        task = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), this, 0L, 20L);
        rotatePowerUpsTask = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), () -> arena.getPowerUps().forEach(PowerUp::rotate), 0L, 1L);
    }

    @Override
    public void run() {

        if (arena.getTimer() > 0) {
            arena.setTimer(arena.getTimer() - 1);
            for (Integer i : Arrays.asList(1,2,3,4,5,10)) {
                if (arena.getTimer() == i) {
                    arena.sendMessage("&c{seconds} &7seconds left in the round!".replace("{seconds}", String.valueOf(arena.getTimer())));
                }
            }
        } else if (arena.getTimer() == 0) {
            Map.Entry<Team, Integer> bestTeam = placedBlocks.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null);
            // NO PLACED BLOCKS
            if (bestTeam == null) {
                arena.sendTitle(40, "&c&lNO WINNER!", "&7A new round will start");
                arena.playSound("ENTITY_PLAYER_LEVELUP");
                arena.setGameState(GameState.ROUND_OVER);
                // DRAW
            } else if (placedBlocks.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), bestTeam.getValue())).count() > 1) {
                arena.sendTitle(40, "&c&lDRAW!", "&7A new round will start");
                arena.playSound("ENTITY_PLAYER_LEVELUP");
                arena.setGameState(GameState.ROUND_OVER);
                // WINNER TEAM FOUND
            } else {
                Team winner = bestTeam.getKey();
                if (winner.getPoints() == arena.getRequiredPoints()) {
                    arena.sendTitle(40, winner.getName(), "&e&lWINNER TEAM!!!");
                    arena.playSound("ENTITY_PLAYER_LEVELUP");
                    arena.setGameState(GameState.RESTARTING);
                } else {
                    if (arena.isLastRound()) {
                        arena.sendTitle(40, winner.getName(), "&e&lWINNER TEAM!!!");
                        arena.playSound("ENTITY_PLAYER_LEVELUP");
                        arena.setGameState(GameState.RESTARTING);
                    } else {
                        arena.sendTitle(40, "{teamcolor}{teamname}".replace("{teamcolor}", winner.getTeamColor().getChatColor().toString()).replace("{teamname}", winner.getName()), "&e&lWINNER");
                        arena.playSound("ENTITY_PLAYER_LEVELUP");
                        arena.setGameState(GameState.ROUND_OVER);
                    }
                }
            }
            placedBlocks.clear();
            task.cancel();
            rotatePowerUpsTask.cancel();
        }

    }

    public void addPlacedBlock(Team team) {
        placedBlocks.merge(team, 1, Integer::sum);
    }

    public void removePlacedBlock(Team team) {
        if (placedBlocks.get(team) == null) return;
        placedBlocks.put(team, placedBlocks.get(team) - 1);
    }

    public void checkWinners() {
        for (Map.Entry<Team, Integer> entry : placedBlocks.entrySet()) {
            if (entry.getValue() != arena.getBlocksRegion().getTotalBlocks()) continue;
            Team team = entry.getKey();
            team.addPoint();
            placedBlocks.clear();
            arena.sendTitle(40, "{teamcolor}{teamname}".replace("{teamcolor}", team.getTeamColor().getChatColor().toString()).replace("{teamname}", team.getName()), "&e&lWINNER");
            arena.playSound("ENTITY_PLAYER_LEVELUP");
            if (team.getPoints() == arena.getRequiredPoints() || arena.isLastRound()) {
                arena.sendTitle(40, team.getName(), "&e&lWINNER TEAM");
                arena.setGameState(GameState.WAITING);
                task.cancel();
                rotatePowerUpsTask.cancel();
                return;
            }
            arena.setGameState(GameState.ROUND_OVER);
            task.cancel();
            rotatePowerUpsTask.cancel();
        }
    }

}
