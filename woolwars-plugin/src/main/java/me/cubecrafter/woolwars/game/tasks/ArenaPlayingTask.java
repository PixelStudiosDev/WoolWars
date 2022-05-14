package me.cubecrafter.woolwars.game.tasks;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.database.PlayerData;
import me.cubecrafter.woolwars.game.arena.Arena;
import me.cubecrafter.woolwars.game.arena.GameState;
import me.cubecrafter.woolwars.game.powerup.PowerUp;
import me.cubecrafter.woolwars.game.team.Team;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class ArenaPlayingTask extends ArenaTask {

    private final BukkitTask rotatePowerUpsTask;
    private final Map<Team, Integer> placedBlocks = new HashMap<>();
    private final List<Block> jumpPads;

    private final Map<Player, Integer> roundKills = new HashMap<>();
    private final Map<Player, Integer> roundPlacedWool = new HashMap<>();
    private final Map<Player, Integer> roundBrokenBlocks = new HashMap<>();

    public ArenaPlayingTask(Arena arena) {
        super(arena);
        jumpPads = arena.getArenaRegion().getBlocks().stream().filter(block -> block.getType().equals(Material.SLIME_BLOCK)).collect(Collectors.toList());
        rotatePowerUpsTask = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), () -> arena.getPowerUps().forEach(PowerUp::rotate), 0L, 1L);
    }

    @Override
    public void execute() {
        if ((arena.getTimer() <= 30 && arena.getTimer() % 10 == 0) || arena.getTimer() <= 5) {
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
        roundKills.forEach(arena::addKills);
        roundBrokenBlocks.forEach(arena::addBrokenBlocks);
        roundPlacedWool.forEach(arena::addPlacedBlocks);
        roundKills.clear();
        roundPlacedWool.clear();
        roundBrokenBlocks.clear();
    }

    @Override
    public int getTaskDuration() {
        return 60;
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
                sendRoundEndedMessages(team, false);
                if (team.getPoints() == arena.getRequiredPoints() || arena.isLastRound()) {
                    cancelTask();
                    rotatePowerUpsTask.cancel();
                    addWinsLossesStats(team);
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
                rotatePowerUpsTask.cancel();
                arena.setGameState(GameState.ROUND_OVER);
                // DRAW
            } else if (placedBlocks.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), bestTeam.getValue())).count() > 1) {
                sendRoundEndedMessages(null, true);
                rotatePowerUpsTask.cancel();
                arena.setGameState(GameState.ROUND_OVER);
                // WINNER TEAM FOUND
            } else {
                Team winner = bestTeam.getKey();
                winner.addPoint();
                sendRoundEndedMessages(winner, false);
                if (winner.getPoints() == arena.getRequiredPoints()) {
                    addWinsLossesStats(winner);
                    rotatePowerUpsTask.cancel();
                    arena.setGameState(GameState.GAME_ENDED);
                } else {
                    if (arena.isLastRound()) {
                        addWinsLossesStats(winner);
                        rotatePowerUpsTask.cancel();
                        arena.setGameState(GameState.GAME_ENDED);
                    } else {
                        rotatePowerUpsTask.cancel();
                        arena.setGameState(GameState.ROUND_OVER);
                    }
                }
            }
        }
    }

    private void sendRoundEndedMessages(Team winner, boolean draw) {
        if (arena.isLastRound()) {
            for (Team team : arena.getTeams()) {
                if (team.equals(winner)) {

                } else {

                }
            }
        } else {
            for (Player player : arena.getPlayers()) {
                player.sendMessage(TextUtil.color("&8&m--------------------------------------------------"));
                player.sendMessage(TextUtil.color("&e               Round #" + arena.getRound() + " stats"));
                if (roundKills.get(player) != null) {
                    player.sendMessage(TextUtil.color("&7Kills: " + roundKills.get(player)));
                }
                if (roundPlacedWool.get(player) != null) {
                    player.sendMessage(TextUtil.color("&7Placed wool: " + roundPlacedWool.get(player)));
                }
                if (roundBrokenBlocks.get(player) != null) {
                    player.sendMessage(TextUtil.color("&7Broken blocks: " + roundBrokenBlocks.get(player)));

                }
                player.sendMessage(TextUtil.color("&8&m--------------------------------------------------"));
            }
        }


        if (draw) {
            arena.sendTitle(40, arena.getTeamPointsFormatted(), "&e&lDRAW");
            arena.playSound("GHAST_MOAN");
            return;
        }
        for (Team team : arena.getTeams()) {
            if (arena.isLastRound()) {
                if (team.equals(winner)) {
                    team.sendTitle(40, "&a&lVICTORY", "&6Your team was victorious!");
                    team.playSound("ENTITY_PLAYER_LEVELUP");
                } else {
                    team.sendTitle(40, "&a&lDEFEAT", "&6Your team was defeated!");
                    team.playSound("GHAST_MOAN");
                }
                continue;
            }
            if (team.equals(winner)) {
                team.sendTitle(40, arena.getTeamPointsFormatted(), "&e&lROUND WON");
                team.playSound("ENTITY_PLAYER_LEVELUP");
            } else {
                team.sendTitle(40, arena.getTeamPointsFormatted(), "&e&lROUND OVER");
                team.playSound("GHAST_MOAN");
            }
        }
    }

    private void addWinsLossesStats(Team winner) {
        for (Team team : arena.getTeams()) {
            if (team.equals(winner)) {
                for (Player player : team.getMembers()) {
                    PlayerData data = ArenaUtil.getPlayerData(player);
                    data.setWins(data.getWins() + 1);
                }
            } else {
                for (Player player : team.getMembers()) {
                    PlayerData data = ArenaUtil.getPlayerData(player);
                    data.setLosses(data.getLosses() + 1);
                }
            }
        }
    }

    public void addKill(Player player) {
        roundKills.merge(player, 1, Integer::sum);
        PlayerData data = ArenaUtil.getPlayerData(player);
        data.setKills(data.getKills() + 1);
    }

    public void addPlacedWool(Player player) {
        roundPlacedWool.merge(player, 1, Integer::sum);
        PlayerData data = ArenaUtil.getPlayerData(player);
        data.setPlacedWool(data.getPlacedWool() + 1);
    }

    public void addBrokenBlock(Player player) {
        roundBrokenBlocks.merge(player, 1, Integer::sum);
        PlayerData data = ArenaUtil.getPlayerData(player);
        data.setBrokenBlocks(data.getBrokenBlocks() + 1);
    }

}
