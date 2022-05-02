package me.cubecrafter.woolwars.game.tasks;

import me.cubecrafter.woolwars.game.arena.Arena;
import me.cubecrafter.woolwars.game.GameState;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ArenaGameEndedTask extends ArenaTask {

    public ArenaGameEndedTask(Arena arena) {
        super(arena);
        arena.setTimer(10);
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
    public void execute() {

    }

    @Override
    public void onTimerEnd() {
        arena.setGameState(GameState.RESTARTING);
    }

}
