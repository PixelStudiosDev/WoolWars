package me.cubecrafter.woolwars.arena.tasks;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.arena.PowerUp;
import me.cubecrafter.woolwars.arena.Team;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ArenaPlayingTask implements Runnable {

    @Getter private final BukkitTask task;
    @Getter private final BukkitTask rotatePowerUpsTask;
    @Getter private final Map<Team, Integer> placedBlocks = new HashMap<>();
    private final Arena arena;
    private final List<Block> jumpPads;

    public ArenaPlayingTask(Arena arena) {
        this.arena = arena;
        arena.setTimer(60);
        jumpPads = arena.getArenaRegion().getBlocks().stream().filter(block -> block.getType().equals(Material.SLIME_BLOCK)).collect(Collectors.toList());
        task = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), this, 0L, 20L);
        rotatePowerUpsTask = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), () -> arena.getPowerUps().forEach(PowerUp::rotate), 0L, 1L);
    }

    @Override
    public void run() {
        if (arena.getTimer() > 0) {
            arena.setTimer(arena.getTimer() - 1);
            for (Integer i : Arrays.asList(1,2,3,4,5,10)) {
                if (arena.getTimer() == i) {
                    arena.sendMessage("&c{seconds} &7seconds left!".replace("{seconds}", String.valueOf(arena.getTimer())));
                }
            }
        } else if (arena.getTimer() == 0) {
            checkWinners();
            placedBlocks.clear();
            task.cancel();
            rotatePowerUpsTask.cancel();
        }
        for (Block block : jumpPads) {
            block.getWorld().playEffect(block.getLocation().add(0, 1.3, 0.5).subtract(-0.5, 0, 0), Effect.HAPPY_VILLAGER, 0);
        }
    }

    public void addPlacedBlock(Team team) {
        if (team == null) return;
        placedBlocks.merge(team, 1, Integer::sum);
    }

    public void removePlacedBlock(Team team) {
        if (placedBlocks.get(team) == null) return;
        placedBlocks.put(team, placedBlocks.get(team) - 1);
        if (placedBlocks.get(team) == 0) placedBlocks.remove(team);
    }

    public void checkWinners() {
        if (arena.getTimer() > 0) {
            for (Map.Entry<Team, Integer> entry : placedBlocks.entrySet()) {
                if (entry.getValue() != arena.getBlocksRegion().getTotalBlocks()) continue;
                Team team = entry.getKey();
                team.addPoint();
                placedBlocks.clear();
                for (Team loop : arena.getTeams()) {
                    if (loop.equals(team)) {
                        loop.sendTitle(40, "&a&lWINNER", arena.getTeamPointsFormatted());
                        loop.playSound("ENTITY_PLAYER_LEVELUP");
                    } else {
                        loop.sendTitle(40, "&c&lLOSER", arena.getTeamPointsFormatted());
                        loop.playSound("GHAST_MOAN");
                    }
                }
                if (team.getPoints() == arena.getRequiredPoints() || arena.isLastRound()) {
                    task.cancel();
                    rotatePowerUpsTask.cancel();
                    arena.setGameState(GameState.GAME_ENDED);
                    return;
                }
                task.cancel();
                rotatePowerUpsTask.cancel();
                arena.setGameState(GameState.ROUND_OVER);
            }
        } else if (arena.getTimer() == 0) {
            Map.Entry<Team, Integer> bestTeam = placedBlocks.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null);
            // NO PLACED BLOCKS
            if (bestTeam == null) {
                arena.setGameState(GameState.ROUND_OVER);
                // DRAW
            } else if (placedBlocks.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), bestTeam.getValue())).count() > 1) {
                arena.sendTitle(40, "&c&lDRAW", arena.getTeamPointsFormatted());
                arena.playSound("GHAST_MOAN");
                arena.setGameState(GameState.ROUND_OVER);
                // WINNER TEAM FOUND
            } else {
                Team winner = bestTeam.getKey();
                winner.addPoint();
                if (winner.getPoints() == arena.getRequiredPoints()) {
                    for (Team loop : arena.getTeams()) {
                        if (loop.equals(winner)) {
                            loop.sendTitle(40, "&a&lWINNER", arena.getTeamPointsFormatted());
                            loop.playSound("ENTITY_PLAYER_LEVELUP");
                        } else {
                            loop.sendTitle(40, "&c&lLOSER", arena.getTeamPointsFormatted());
                            loop.playSound("GHAST_MOAN");
                        }
                    }
                    arena.setGameState(GameState.GAME_ENDED);
                } else {
                    for (Team loop : arena.getTeams()) {
                        if (loop.equals(winner)) {
                            loop.sendTitle(40, "&a&lWINNER", arena.getTeamPointsFormatted());
                            loop.playSound("ENTITY_PLAYER_LEVELUP");
                        } else {
                            loop.sendTitle(40, "&c&lLOSER", arena.getTeamPointsFormatted());
                            loop.playSound("GHAST_MOAN");
                        }
                    }
                    if (arena.isLastRound()) {
                        arena.setGameState(GameState.GAME_ENDED);
                    } else {
                        arena.setGameState(GameState.ROUND_OVER);
                    }
                }
            }
        }
    }

}
