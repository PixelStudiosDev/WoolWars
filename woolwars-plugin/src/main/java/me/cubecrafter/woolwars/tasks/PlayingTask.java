package me.cubecrafter.woolwars.tasks;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.api.arena.GameState;
import me.cubecrafter.woolwars.arena.GameArena;
import me.cubecrafter.woolwars.api.database.PlayerData;
import me.cubecrafter.woolwars.powerup.PowerUp;
import me.cubecrafter.woolwars.team.GameTeam;
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
public class PlayingTask extends ArenaTask {

    private final BukkitTask rotatePowerUpsTask;
    private final Map<GameTeam, Integer> placedBlocks = new HashMap<>();
    private final List<Block> jumpPads;
    private final Map<Player, Integer> roundKills = new HashMap<>();
    private final Map<Player, Integer> roundPlacedWool = new HashMap<>();
    private final Map<Player, Integer> roundBrokenBlocks = new HashMap<>();

    public PlayingTask(GameArena arena, int duration) {
        super(arena, duration);
        jumpPads = arena.getArenaRegion().getBlocks().stream().filter(block -> block.getType().equals(Material.SLIME_BLOCK)).collect(Collectors.toList());
        rotatePowerUpsTask = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), () -> arena.getPowerUps().forEach(PowerUp::rotate), 0L, 1L);
    }

    @Override
    public void execute() {
        if ((arena.getTimer() <= 30 && arena.getTimer() % 10 == 0) || arena.getTimer() <= 5) {
            TextUtil.sendMessage(arena.getPlayers(), "&c{seconds} &7seconds left!".replace("{seconds}", String.valueOf(arena.getTimer())));
        }
        for (Block block : jumpPads) {
            block.getWorld().playEffect(block.getLocation().add(0, 1.3, 0.5).subtract(-0.5, 0, 0), Effect.HAPPY_VILLAGER, 0);
        }
    }

    @Override
    public void onEnd() {
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

    public void addPlacedWool(GameTeam team) {
        if (team == null) return;
        placedBlocks.merge(team, 1, Integer::sum);
    }

    public void removePlacedWool(GameTeam team) {
        if (placedBlocks.get(team) == null) return;
        placedBlocks.put(team, placedBlocks.get(team) - 1);
        if (placedBlocks.get(team) == 0) placedBlocks.remove(team);
    }

    public void checkWinners() {
        if (arena.getTimer() > 0) {
            for (Map.Entry<GameTeam, Integer> entry : placedBlocks.entrySet()) {
                if (entry.getValue() != arena.getWoolRegion().getTotalBlocks()) continue;
                GameTeam team = entry.getKey();
                team.addPoint();
                placedBlocks.clear();
                if (team.getPoints() == arena.getRequiredPoints() || arena.isLastRound()) {
                    cancelTask();
                    rotatePowerUpsTask.cancel();
                    addWinsLossesStats(team);
                    sendRoundEndedMessages(team, false, true);
                    arena.setGameState(GameState.GAME_ENDED);
                    return;
                }
                sendRoundEndedMessages(team, false, false);
                cancelTask();
                rotatePowerUpsTask.cancel();
                arena.setGameState(GameState.ROUND_OVER);
            }
        } else if (arena.getTimer() == 0) {
            Map.Entry<GameTeam, Integer> bestTeam = placedBlocks.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null);
            // NO PLACED BLOCKS
            if (bestTeam == null) {
                sendRoundEndedMessages(null, false, false);
                rotatePowerUpsTask.cancel();
                arena.setGameState(GameState.ROUND_OVER);
                // DRAW
            } else if (placedBlocks.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), bestTeam.getValue())).count() > 1) {
                sendRoundEndedMessages(null, true, false);
                rotatePowerUpsTask.cancel();
                arena.setGameState(GameState.ROUND_OVER);
                // WINNER TEAM FOUND
            } else {
                GameTeam winner = bestTeam.getKey();
                winner.addPoint();
                if (winner.getPoints() == arena.getRequiredPoints()) {
                    addWinsLossesStats(winner);
                    rotatePowerUpsTask.cancel();
                    sendRoundEndedMessages(winner, false, true);
                    arena.setGameState(GameState.GAME_ENDED);
                } else {
                    if (arena.isLastRound()) {
                        addWinsLossesStats(winner);
                        rotatePowerUpsTask.cancel();
                        sendRoundEndedMessages(winner, false, true);
                        arena.setGameState(GameState.GAME_ENDED);
                    } else {
                        rotatePowerUpsTask.cancel();
                        sendRoundEndedMessages(winner, false, false);
                        arena.setGameState(GameState.ROUND_OVER);
                    }
                }
            }
        }
    }

    private void sendRoundEndedMessages(GameTeam winner, boolean draw, boolean lastRound) {
        if (lastRound) {
            for (GameTeam team : arena.getTeams()) {
                team.sendMessage("&8&m--------------------------------------------------");
                team.sendMessage("&f&l                 WOOL WARS");
                team.sendMessage("");
                team.sendMessage(team.equals(winner) ? "&a          Your team won!" : "&c          Your team lost!");
                team.sendMessage("");
                team.sendMessage("&e&lMost Kills &7- " + roundKills.entrySet().stream().max(Map.Entry.comparingByValue()).map(entry -> entry.getKey().getDisplayName() + " &7- " + entry.getValue()).orElse("None"));
                team.sendMessage("&6&lMost Wool Placed &7- " + roundPlacedWool.entrySet().stream().max(Map.Entry.comparingByValue()).map(entry -> entry.getKey().getDisplayName() + " &7- " + entry.getValue()).orElse("None"));
                team.sendMessage("&c&lMost Blocks Broken &7- " + roundBrokenBlocks.entrySet().stream().max(Map.Entry.comparingByValue()).map(entry -> entry.getKey().getDisplayName() + " &7- " + entry.getValue()).orElse("None"));
                team.sendMessage("");
                team.sendMessage("&8&m--------------------------------------------------");
            }
        } else {
            for (Player player : arena.getPlayers()) {
                player.sendMessage(TextUtil.color("&8&m--------------------------------------------------"));
                player.sendMessage(TextUtil.color("&e               Round #" + arena.getRound() + " stats"));
                if (roundBrokenBlocks.get(player) == null && roundPlacedWool.get(player) == null && roundKills.get(player) == null) {
                    TextUtil.sendMessage(player, "&cNo stats achieved for this round!");
                } else {
                    if (roundKills.get(player) != null) {
                        player.sendMessage(TextUtil.color("&7Kills: " + roundKills.get(player)));
                    }
                    if (roundPlacedWool.get(player) != null) {
                        player.sendMessage(TextUtil.color("&7Placed wool: " + roundPlacedWool.get(player)));
                    }
                    if (roundBrokenBlocks.get(player) != null) {
                        player.sendMessage(TextUtil.color("&7Broken blocks: " + roundBrokenBlocks.get(player)));

                    }
                }
                player.sendMessage(TextUtil.color("&8&m--------------------------------------------------"));
            }
        }


        if (draw) {
            TextUtil.sendTitle(arena.getPlayers(), 2, arena.getTeamPointsFormatted(), "&e&lDRAW");
            ArenaUtil.playSound(arena.getPlayers(), "GHAST_MOAN");
            return;
        }
        for (GameTeam team : arena.getTeams()) {
            if (lastRound) {
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

    private void addWinsLossesStats(GameTeam winner) {
        for (GameTeam team : arena.getTeams()) {
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
