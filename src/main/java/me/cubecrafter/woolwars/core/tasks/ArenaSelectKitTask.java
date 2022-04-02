package me.cubecrafter.woolwars.core.tasks;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.Titles;
import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.core.Arena;
import me.cubecrafter.woolwars.core.GameState;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class ArenaSelectKitTask implements Runnable {

    @Getter private final BukkitTask task;
    private final Arena arena;
    private int timer = 10;

    public ArenaSelectKitTask(Arena arena) {
        this.arena = arena;
        task = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), this, 0L, 20L);
        ItemStack kitItem = new ItemBuilder("BLAZE_POWDER").setDisplayName("&eSelect Kit").build();
        for (Player player : arena.getPlayers()) {
            player.getInventory().setItem(0, kitItem);
        }
    }

    @Override
    public void run() {
        if (timer == 0) {
            arena.setGameState(GameState.PLAYING);
            for (Player player : arena.getPlayers()) {
                Titles.sendTitle(player, 0, 40, 0, TextUtil.color("&a&lROUND START"), TextUtil.color("&bRound {round}".replace("{round}", String.valueOf(arena.getRound()))));
                XSound.play(player, "BLOCK_ANVIL_LAND");
            }
            task.cancel();
        } else if (timer <= 3 && timer > 0) {
            for (Player player : arena.getPlayers()) {
                Titles.sendTitle(player, 0, 20, 0, TextUtil.color("&c&l{seconds}".replace("{seconds}", String.valueOf(timer))), TextUtil.color("&6Get Ready"));
                XSound.play(player, "ENTITY_CHICKEN_EGG");
            }
            timer--;
        } else {
            timer--;
        }
    }

    public String getTimerFormatted() {
        int minutes = (timer / 60) % 60;
        int seconds = (timer) % 60;
        return minutes + ":" + (seconds > 9 ? seconds : "0" + seconds);
    }

}
