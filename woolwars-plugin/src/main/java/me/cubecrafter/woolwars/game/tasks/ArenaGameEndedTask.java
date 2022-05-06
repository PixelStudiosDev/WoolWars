package me.cubecrafter.woolwars.game.tasks;

import me.cubecrafter.woolwars.game.arena.Arena;
import me.cubecrafter.woolwars.game.arena.GameState;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class ArenaGameEndedTask extends ArenaTask {

    public ArenaGameEndedTask(Arena arena) {
        super(arena);
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
        ItemStack playAgainItem = new ItemBuilder("PAPER").setDisplayName("&dPlay Again").setTag("playagain-item").build();
        ItemStack leaveItem = new ItemBuilder("RED_BED").setDisplayName("&cReturn to Lobby").setLore(Arrays.asList("&7Click to return to the lobby!")).setTag("leave-item").build();
        for (Player player : arena.getPlayers()) {
            player.getInventory().setItem(7, playAgainItem);
            player.getInventory().setItem(8, leaveItem);
        }
    }

    @Override
    public void execute() {

    }

    @Override
    public void onTimerEnd() {
        arena.setGameState(GameState.RESTARTING);
    }

    @Override
    public int getTaskDuration() {
        return 10;
    }

}
