package me.cubecrafter.woolwars.arena.tasks;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.arena.PowerUp;
import me.cubecrafter.woolwars.arena.Team;
import me.cubecrafter.woolwars.kits.Kit;
import me.cubecrafter.woolwars.utils.GameUtil;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ArenaPreRoundTask implements Runnable {

    @Getter private final BukkitTask task;
    private final Arena arena;
    @Getter private final List<Player> kitSelected = new ArrayList<>();

    public ArenaPreRoundTask(Arena arena) {
        this.arena = arena;
        arena.setTimer(10);
        arena.setRound(arena.getRound() + 1);
        arena.killEntities();
        task = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), this, 0L, 20L);
        ItemStack kitItem = new ItemBuilder("CHEST").setDisplayName("&eSelect Kit").setNBT("woolwars", "kit-item").build();
        for (Player player : arena.getDeadPlayers()) {
            player.setFlying(false);
            player.setAllowFlight(false);
            player.spigot().setCollidesWithEntities(true);
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        }
        arena.getDeadPlayers().clear();
        arena.getPlacedBlocks().forEach(block -> block.setType(Material.AIR));
        arena.getPlacedBlocks().clear();
        for (Team team : arena.getTeams()) {
            team.getBarrier().fill(team.getBarrierBlock());
            team.teleportToSpawn();
        }
        for (Player player : arena.getPlayers()) {
            player.getInventory().clear();
            player.getInventory().setItem(0, kitItem);
        }
        for (Block block : arena.getBlocksRegion().getBlocks()) {
            List<String> defaultBlocks = new ArrayList<>(Arrays.asList("QUARTZ_BLOCK", "SNOW_BLOCK", "WHITE_WOOL"));
            block.setType(XMaterial.matchXMaterial(defaultBlocks.get(new Random().nextInt(defaultBlocks.size()))).get().parseMaterial());
        }
    }

    @Override
    public void run() {
        if (arena.getTimer() == 0) {
            if (arena.isLastRound()) {
                arena.sendTitle(40, "&a&lROUND START", "&bLast Round!");
            } else if (arena.isExtraRound()) {
                arena.sendTitle(40, "&a&lROUND START", "&bExtra Round!");
            } else {
                arena.sendTitle(40, "&a&lROUND START", "&bRound {round}".replace("{round}", String.valueOf(arena.getRound())));
            }
            Kit defaultKit = GameUtil.getKits().stream().filter(Kit::isDefaultKit).findAny().orElse(null);
            arena.getPlayers().stream().filter(player -> !kitSelected.contains(player)).forEach(player -> {
                defaultKit.addToPlayer(player, arena.getTeamByPlayer(player));
            });
            kitSelected.clear();
            arena.playSound("BLOCK_ANVIL_LAND");
            arena.getTeams().forEach(team -> team.getBarrier().clear());
            arena.getPowerUps().forEach(PowerUp::spawn);
            arena.setGameState(GameState.PLAYING);
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
