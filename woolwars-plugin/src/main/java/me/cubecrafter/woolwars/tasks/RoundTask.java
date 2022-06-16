package me.cubecrafter.woolwars.tasks;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.particles.XParticle;
import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.api.arena.GameState;
import me.cubecrafter.woolwars.api.database.PlayerData;
import me.cubecrafter.woolwars.arena.GameArena;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.powerup.PowerUp;
import me.cubecrafter.woolwars.team.GameTeam;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class RoundTask extends ArenaTask {

    private BukkitTask rotatePowerUpsTask;
    private final Map<GameTeam, Integer> placedWool = new HashMap<>();
    private List<Block> jumpPads;
    private final Map<Player, Integer> roundKills = new HashMap<>();
    private final Map<Player, Integer> roundPlacedWool = new HashMap<>();
    private final Map<Player, Integer> roundBrokenBlocks = new HashMap<>();

    public RoundTask(GameArena arena) {
        super(arena);
    }

    @Override
    public void execute() {
        if ((arena.getTimer() <= 30 && arena.getTimer() % 10 == 0) || arena.getTimer() <= 5) {
            TextUtil.sendMessage(arena.getPlayers(), "&c{seconds} &7seconds left!".replace("{seconds}", String.valueOf(arena.getTimer())));
        }
        if (Configuration.JUMP_PADS_PARTICLES_ENABLED.getAsBoolean()) {
            for (Block block : jumpPads) {
                block.getWorld().playEffect(block.getLocation().add(0.5, 1.3, 0.5), Effect.valueOf(Configuration.JUMP_PADS_PARTICLES_TYPE.getAsString()), 0);
            }
        }
    }

    @Override
    public void onEnd() {
        Map.Entry<GameTeam, Integer> bestTeam = placedWool.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null);
        // NO PLACED BLOCKS
        if (bestTeam == null) {
            sendRoundEndedMessages(null, false, false);
            arena.setGameState(GameState.ROUND_OVER);
            // DRAW
        } else if (placedWool.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), bestTeam.getValue())).count() > 1) {
            sendRoundEndedMessages(null, true, false);
            arena.setGameState(GameState.ROUND_OVER);
            // WINNER TEAM FOUND
        } else {
            GameTeam winner = bestTeam.getKey();
            winner.addPoint();
            if (winner.getPoints() == arena.getRequiredPoints()) {
                addWinsLossesStats(winner);
                sendRoundEndedMessages(winner, false, true);
                arena.setGameState(GameState.GAME_ENDED);
            } else {
                sendRoundEndedMessages(winner, false, false);
                arena.setGameState(GameState.ROUND_OVER);
            }
        }
        rotatePowerUpsTask.cancel();
        addRoundStats();
    }

    private void addRoundStats() {
        roundKills.forEach(arena::addKills);
        roundBrokenBlocks.forEach(arena::addBrokenBlocks);
        roundPlacedWool.forEach(arena::addPlacedWool);
    }

    @Override
    public int getDuration() {
        return Configuration.ACTIVE_ROUND_COUNTDOWN.getAsInt();
    }

    @Override
    public void onStart() {
        jumpPads = arena.getArenaRegion().getBlocks().stream()
                .filter(block -> block.getType().equals(XMaterial.matchXMaterial(Configuration.JUMP_PADS_TOP_BLOCK.getAsString()).get().parseMaterial()))
                .filter(block -> block.getRelative(BlockFace.DOWN).getType().equals(XMaterial.matchXMaterial(Configuration.JUMP_PADS_BOTTOM_BLOCK.getAsString()).get().parseMaterial()))
                .collect(Collectors.toList());
        rotatePowerUpsTask = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), () -> arena.getPowerUps().forEach(PowerUp::rotate), 0L, 1L);
    }

    public void addPlacedWool(GameTeam team) {
        if (team == null) return;
        placedWool.merge(team, 1, Integer::sum);
    }

    public void removePlacedWool(GameTeam team) {
        if (placedWool.get(team) == null) return;
        placedWool.put(team, placedWool.get(team) - 1);
        if (placedWool.get(team) == 0) placedWool.remove(team);
    }

    public void checkWinners() {
        for (Map.Entry<GameTeam, Integer> entry : placedWool.entrySet()) {
            if (entry.getValue() < arena.getWoolRegion().getTotalBlocks()) continue;
            GameTeam team = entry.getKey();
            team.addPoint();
            if (team.getPoints() == arena.getRequiredPoints()) {
                addWinsLossesStats(team);
                sendRoundEndedMessages(team, false, true);
                arena.setGameState(GameState.GAME_ENDED);
                return;
            } else {
                sendRoundEndedMessages(team, false, false);
                arena.setGameState(GameState.ROUND_OVER);
            }
            addRoundStats();
            rotatePowerUpsTask.cancel();
            cancel();
        }
    }


    private void sendRoundEndedMessages(GameTeam winner, boolean draw, boolean lastRound) {
        if (lastRound) {
            for (GameTeam team : arena.getTeams()) {
                TextUtil.sendMessage(team.getMembers(),"&8&m--------------------------------------------------");
                TextUtil.sendMessage(team.getMembers(),"");
                TextUtil.sendMessage(team.getMembers(), team.equals(winner) ? "&a          Your team won!" : "&c          Your team lost!");
                TextUtil.sendMessage(team.getMembers(),"");
                TextUtil.sendMessage(team.getMembers(),"&e&lMost Kills &7- " + roundKills.entrySet().stream().max(Map.Entry.comparingByValue()).map(entry -> entry.getKey().getDisplayName() + " &7- " + entry.getValue()).orElse("None"));
                TextUtil.sendMessage(team.getMembers(),"&6&lMost Wool Placed &7- " + roundPlacedWool.entrySet().stream().max(Map.Entry.comparingByValue()).map(entry -> entry.getKey().getDisplayName() + " &7- " + entry.getValue()).orElse("None"));
                TextUtil.sendMessage(team.getMembers(),"&c&lMost Blocks Broken &7- " + roundBrokenBlocks.entrySet().stream().max(Map.Entry.comparingByValue()).map(entry -> entry.getKey().getDisplayName() + " &7- " + entry.getValue()).orElse("None"));
                TextUtil.sendMessage(team.getMembers(),"");
                TextUtil.sendMessage(team.getMembers(),"&8&m--------------------------------------------------");
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
            ArenaUtil.playSound(arena.getPlayers(), Configuration.SOUNDS_ROUND_DRAW.getAsString());
            return;
        }
        for (GameTeam team : arena.getTeams()) {
            if (lastRound) {
                if (team.equals(winner)) {
                    TextUtil.sendTitle(team.getMembers(), 2, "&a&lVICTORY", "&6Your team was victorious!");
                    ArenaUtil.playSound(team.getMembers(), Configuration.SOUNDS_GAME_WON.getAsString());
                } else {
                    TextUtil.sendTitle(team.getMembers(), 2, "&a&lDEFEAT", "&6Your team was defeated!");
                    ArenaUtil.playSound(team.getMembers(), Configuration.SOUNDS_GAME_LOST.getAsString());
                }
                continue;
            }
            if (team.equals(winner)) {
                TextUtil.sendTitle(team.getMembers(), 2, arena.getTeamPointsFormatted(), "&e&lROUND WON");
                ArenaUtil.playSound(team.getMembers(), Configuration.SOUNDS_ROUND_WON.getAsString());
            } else {
                TextUtil.sendTitle(team.getMembers(), 2, arena.getTeamPointsFormatted(), "&e&lROUND OVER");
                ArenaUtil.playSound(team.getMembers(), Configuration.SOUNDS_ROUND_LOST.getAsString());
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
