package me.cubecrafter.woolwars.arena.tasks;

import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.database.PlayerData;
import me.cubecrafter.woolwars.powerup.PowerUp;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.VersionUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GameEndTask extends ArenaTask {

    public GameEndTask(Arena arena) {
        super(arena, Configuration.GAME_END_DURATION.getAsInt());
    }

    @Override
    public void execute() {}

    @Override
    public void onEnd() {
        arena.restart();
    }

    @Override
    public void onStart() {
        arena.getPowerUps().forEach(PowerUp::remove);
        for (Player player : arena.getAlivePlayers()) {
            player.setAllowFlight(true);
            player.setFlying(true);
            player.getInventory().setArmorContents(null);
            player.getInventory().clear();
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
            player.setFireTicks(0);
            player.setHealth(20);
            for (Player alive : arena.getAlivePlayers()) {
                VersionUtil.hidePlayer(alive, player);
            }
            for (Player dead : arena.getDeadPlayers()) {
                VersionUtil.showPlayer(player, dead);
            }
        }
        ItemStack playAgainItem = ItemBuilder.fromConfig(Configuration.PLAY_AGAIN_ITEM.getAsSection()).setTag("playagain-item").build();
        ItemStack leaveItem = ItemBuilder.fromConfig(Configuration.LEAVE_ITEM.getAsSection()).setTag("leave-item").build();
        for (Player player : arena.getPlayers()) {
            player.getInventory().setItem(7, playAgainItem);
            player.getInventory().setItem(8, leaveItem);
            PlayerData data = ArenaUtil.getPlayerData(player);
            data.setGamesPlayed(data.getGamesPlayed() + 1);
        }
    }

}
