package me.cubecrafter.woolwars.core.tasks;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.core.Arena;
import me.cubecrafter.woolwars.core.Team;
import me.cubecrafter.woolwars.utils.GameUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class ArenaPlayingTask implements Runnable, Listener {

    @Getter private final BukkitTask task;
    private final Arena arena;
    private final HashMap<Team, Integer> placedBlocks = new HashMap<>();

    public ArenaPlayingTask(Arena arena) {
        Bukkit.getServer().getPluginManager().registerEvents(this, WoolWars.getInstance());
        this.arena = arena;
        task = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), this, 0L, 20L);
    }

    @Override
    public void run() {

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        if (arena.getBlocksRegion().isInside(e.getBlock().getLocation()) && GameUtil.isPlaying(player)) {
            e.getPlayer().sendMessage("placed inside");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (arena.getBlocksRegion().isInside(e.getBlock().getLocation()) && GameUtil.isPlaying(player)) {
            e.getPlayer().sendMessage("broken inside");
        }
    }

}
