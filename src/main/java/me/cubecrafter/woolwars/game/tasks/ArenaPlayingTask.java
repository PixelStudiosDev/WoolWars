package me.cubecrafter.woolwars.game.tasks;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.game.arena.Arena;
import me.cubecrafter.woolwars.game.GameState;
import me.cubecrafter.woolwars.game.powerup.PowerUp;
import me.cubecrafter.woolwars.game.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ArenaPlayingTask extends ArenaTask {

    @Getter private final BukkitTask rotatePowerUpsTask;
    @Getter private final Map<Team, Integer> placedBlocks = new HashMap<>();
    private final List<Block> jumpPads;

    public ArenaPlayingTask(Arena arena) {
        super(arena);
        arena.setTimer(60);
        jumpPads = arena.getArenaRegion().getBlocks().stream().filter(block -> block.getType().equals(Material.SLIME_BLOCK)).collect(Collectors.toList());
        rotatePowerUpsTask = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), () -> arena.getPowerUps().forEach(PowerUp::rotate), 0L, 1L);
    }

    @Override
    public void execute() {
        if (arena.getTimer() == 10 || arena.getTimer() <= 5) {
            arena.sendMessage("&c{seconds} &7seconds left!".replace("{seconds}", String.valueOf(arena.getTimer())));
        }
        for (Block block : jumpPads) {
            block.getWorld().playEffect(block.getLocation().add(0, 1.3, 0.5).subtract(-0.5, 0, 0), Effect.HAPPY_VILLAGER, 0);
        }
    }

    @Override
    public void onTimerEnd() {
        checkWinners();
        placedBlocks.clear();
        rotatePowerUpsTask.cancel();
    }

    public void addPlacedWool(Team team) {
        if (team == null) return;
        placedBlocks.merge(team, 1, Integer::sum);
    }

    public void removePlacedWool(Team team) {
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
                    cancelTask();
                    rotatePowerUpsTask.cancel();
                    arena.setGameState(GameState.GAME_ENDED);
                    return;
                }
                cancelTask();
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
