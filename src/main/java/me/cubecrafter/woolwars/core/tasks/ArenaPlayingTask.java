package me.cubecrafter.woolwars.core.tasks;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.Titles;
import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.core.Arena;
import me.cubecrafter.woolwars.core.GameState;
import me.cubecrafter.woolwars.core.Team;
import me.cubecrafter.woolwars.utils.GameUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ArenaPlayingTask implements Runnable, Listener {

    @Getter private final BukkitTask task;
    private final Arena arena;
    private final Map<Team, Integer> placedBlocks = new HashMap<>();

    public ArenaPlayingTask(Arena arena) {
        Bukkit.getServer().getPluginManager().registerEvents(this, WoolWars.getInstance());
        this.arena = arena;
        arena.setTimer(60);
        task = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), this, 0L, 20L);
    }

    @Override
    public void run() {
        if (arena.getTimer() > 0) {
            arena.setTimer(arena.getTimer() - 1);
        } else if (arena.getTimer() == 0) {
            arena.broadcast(getBestTeam() != null ? getBestTeam().getName() : "Best Team is null");
            task.cancel();
        }
    }

    public Team getBestTeam() {
        return placedBlocks.keySet().stream().max(Comparator.comparing(placedBlocks::get)).orElse(null);
    }

    public void checkForWinners() {
        for (Map.Entry<Team, Integer> entry : placedBlocks.entrySet()) {
            if (entry.getValue() == arena.getBlocksRegion().getTotalBlocks()) {
                Team team = entry.getKey();
                team.addPoint();
                placedBlocks.clear();
                arena.getBlocksRegion().clear();
                for (Player player : arena.getPlayers()) {
                    Titles.sendTitle(player, 0, 40, 0, TextUtil.color("{teamcolor}{teamname}".replace("{teamcolor}", team.getTeamColor().getChatColor().toString()).replace("{teamname}", team.getName())), TextUtil.color("&e&lWINNER"));
                    XSound.play(player, "ENTITY_PLAYER_LEVELUP");
                }
                if (team.getPoints() == arena.getRequiredPoints()) {
                    arena.getPlayers().forEach(player -> Titles.sendTitle(player, 0, 40, 0, TextUtil.color(team.getName()), TextUtil.color("&e&lWINNER TEAM")));
                    arena.restart();
                    task.cancel();
                    return;
                }
                arena.setGameState(GameState.PRE_ROUND);
                task.cancel();
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        if (arena.getBlocksRegion().isInside(e.getBlock().getLocation()) && GameUtil.isPlaying(player)) {
            Team team = arena.getTeamByWool(e.getBlock().getType());
            if (team == null) return;
            if (e.getBlock().getType().equals(team.getTeamColor().getWoolMaterial())) {
                player.sendMessage("scored");
                placedBlocks.merge(team, 1, Integer::sum);
            }
            checkForWinners();
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (arena.getBlocksRegion().isInside(e.getBlock().getLocation()) && GameUtil.isPlaying(player)) {
            Team team = arena.getTeamByWool(e.getBlock().getType());
            if (team == null) return;
            if (e.getBlock().getType().equals(team.getTeamColor().getWoolMaterial())) {
                player.sendMessage("removed score");
                placedBlocks.put(team, placedBlocks.get(team) - 1);
            }
        }
    }

}
