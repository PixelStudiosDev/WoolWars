package me.cubecrafter.woolwars.arena.tasks;

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
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;

public class ArenaPreRoundTask implements Runnable {

    @Getter private final BukkitTask task;
    private final Arena arena;
    @Getter private final Set<Player> kitSelected = new HashSet<>();

    public ArenaPreRoundTask(Arena arena) {
        this.arena = arena;
        arena.setTimer(10);
        arena.setRound(arena.getRound() + 1);
        arena.killEntities();
        arena.resetBlocks();
        arena.respawnPlayers();
        ItemStack kitItem = new ItemBuilder("CHEST").setDisplayName("&eSelect Kit").setNBT("woolwars", "kit-item").build();
        for (Player player : arena.getPlayers()) {
            player.getInventory().clear();
            player.getInventory().setItem(0, kitItem);
        }
        task = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), this, 0L, 20L);
    }

    @Override
    public void run() {
        if (arena.getTimer() == 0) {
            arena.getPlayers().forEach(HumanEntity::closeInventory);
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
            arena.getTeams().forEach(Team::removeBarrier);
            arena.getPowerUps().forEach(PowerUp::spawn);
            arena.setGameState(GameState.PLAYING);
            task.cancel();
        } else if (arena.getTimer() <= 3) {
            arena.sendTitle(20, "&c{seconds}".replace("{seconds}", String.valueOf(arena.getTimer())), "&7Get Ready");
            arena.playSound("ENTITY_CHICKEN_EGG");
            arena.setTimer(arena.getTimer() - 1);
        } else {
            arena.setTimer(arena.getTimer() - 1);
        }
    }

}
