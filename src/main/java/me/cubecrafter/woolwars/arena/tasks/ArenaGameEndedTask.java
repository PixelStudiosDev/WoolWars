package me.cubecrafter.woolwars.arena.tasks;

import com.cryptomorin.xseries.messages.Titles;
import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

public class ArenaGameEndedTask implements Runnable {

    @Getter private final BukkitTask task;
    private final Arena arena;

    public ArenaGameEndedTask(Arena arena) {
        this.arena = arena;
        arena.setTimer(10);
        task = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), this, 0L, 20L);
        for (Player player : arena.getAlivePlayers()) {
            player.setAllowFlight(true);
            player.setFlying(true);
            player.getInventory().setArmorContents(null);
            player.getInventory().clear();
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
            player.setFireTicks(0);
            player.setHealth(20);
            ArenaUtil.hideDeadPlayer(player, arena);
        }
    }

    @Override
    public void run() {
        if (arena.getTimer() > 0) {
            arena.setTimer(arena.getTimer() - 1);
        } else if (arena.getTimer() == 0) {
            arena.setGameState(GameState.RESTARTING);
            task.cancel();
        }
    }


}
