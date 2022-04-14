package me.cubecrafter.woolwars.core.tasks;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.core.Arena;
import me.cubecrafter.woolwars.core.GameState;
import me.cubecrafter.woolwars.core.Team;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class ArenaPreRoundTask implements Runnable {

    @Getter private final BukkitTask task;
    private final Arena arena;

    public ArenaPreRoundTask(Arena arena) {
        this.arena = arena;
        arena.setTimer(10);
        arena.setRound(arena.getRound() + 1);
        task = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), this, 0L, 20L);
        ItemStack kitItem = new ItemBuilder("CHEST").setDisplayName("&eSelect Kit").setNBT("woolwars", "select_kit").build();
        for (Player player : arena.getDeadPlayers()) {
            player.setFlying(false);
            player.setAllowFlight(false);
            player.spigot().setCollidesWithEntities(true);
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        }
        arena.getDeadPlayers().clear();
        arena.getPlacedBlocks().forEach(block -> block.setType(Material.AIR));
        arena.getPlacedBlocks().clear();
        arena.getBlocksRegion().clear();
        for (Team team : arena.getTeams()) {
            team.getBarrier().fill(team.getBarrierBlock());
            team.teleportToSpawn();
            team.applyArmor();
        }
        /*
        for (Player player : arena.getPlayers()) {
            player.getInventory().setItem(0, kitItem);
        }
         */
    }

    @Override
    public void run() {
        if (arena.getTimer() == 0) {
            if (arena.isLastRound()) {
                arena.sendTitle(40, "&a&lROUND START", "&bLast Round!");
            } else {
                arena.sendTitle(40, "&a&lROUND START", "&bRound {round}".replace("{round}", String.valueOf(arena.getRound())));
            }
            arena.setGameState(GameState.PLAYING);
            arena.playSound("BLOCK_ANVIL_LAND");
            arena.getTeams().forEach(team -> team.getBarrier().clear());
            task.cancel();
        } else if (arena.getTimer() <= 3) {
            arena.sendTitle(20, "&c&l{seconds}".replace("{seconds}", String.valueOf(arena.getTimer())), "&6Get Ready");
            arena.playSound("ENTITY_CHICKEN_EGG");
            arena.setTimer(arena.getTimer() - 1);
        } else {
            arena.setTimer(arena.getTimer() - 1);
        }
    }

}
