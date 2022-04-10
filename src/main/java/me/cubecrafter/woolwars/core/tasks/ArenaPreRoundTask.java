package me.cubecrafter.woolwars.core.tasks;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.core.Arena;
import me.cubecrafter.woolwars.core.GameState;
import me.cubecrafter.woolwars.core.Team;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class ArenaPreRoundTask implements Runnable {

    @Getter private final BukkitTask task;
    private final Arena arena;

    public ArenaPreRoundTask(Arena arena) {
        this.arena = arena;
        arena.setTimer(10);
        task = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), this, 0L, 20L);
        ItemStack kitItem = new ItemBuilder("CHEST").setDisplayName("&eSelect Kit").setNBT("id", "select_kit").build();
        arena.setRound(arena.getRound() + 1);
        for (Player player : arena.getDeadPlayers()) {
            player.setFlying(false);
            player.setAllowFlight(false);
            player.spigot().setCollidesWithEntities(true);
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        }
        arena.getDeadPlayers().clear();
        for (Team team : arena.getTeams()) {
            team.teleportToSpawn();
        }
        for (Player player : arena.getPlayers()) {
            player.getInventory().setItem(0, kitItem);
        }
    }

    @Override
    public void run() {
        if (arena.getTimer() == 0) {
            arena.setGameState(GameState.PLAYING);
            arena.sendTitle(40, "&a&lROUND START", "&bRound {round}".replace("{round}", String.valueOf(arena.getRound())));
            arena.playSound("BLOCK_ANVIL_LAND");
            /*
            for (Team team : arena.getTeams().values()) {
                for (Player player : team.getMembers()) {
                    List<ItemStack> items = WoolWars.getInstance().getPlayerDataHandler().getPlayerData(player.getUniqueId()).getKit().getItems();
                    for (ItemStack item : items) {
                        if (item.getType().toString().contains("WOOL")) {
                            ItemStack coloredWool = new ItemBuilder(team.getTeamColor().getWoolMaterial()).setAmount(64).build();
                            player.getInventory().addItem(coloredWool);
                        } else {
                            player.getInventory().addItem(item);
                        }
                    }
                }
            }

             */
            task.cancel();
        } else if (arena.getTimer() <= 3 && arena.getTimer() > 0) {
            arena.sendTitle(20, "&c&l{seconds}".replace("{seconds}", String.valueOf(arena.getTimer())), "&6Get Ready");
            arena.playSound("ENTITY_CHICKEN_EGG");
            arena.setTimer(arena.getTimer() - 1);
        } else {
            arena.setTimer(arena.getTimer() - 1);
        }
    }

}
