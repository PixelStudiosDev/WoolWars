package me.cubecrafter.woolwars.tasks;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.api.database.PlayerData;
import me.cubecrafter.woolwars.api.nms.NMS;
import me.cubecrafter.woolwars.api.powerup.PowerUp;
import me.cubecrafter.woolwars.arena.GameArena;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.powerup.GamePowerUp;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GameEndedTask extends ArenaTask {

    public GameEndedTask(GameArena arena) {
        super(arena);
    }

    @Override
    public void execute() {
    }

    @Override
    public void onEnd() {
        arena.restart();
    }

    @Override
    public int getDuration() {
        return Configuration.GAME_END_DURATION.getAsInt();
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
            NMS nms = WoolWars.getInstance().getNms();
            for (Player alive : arena.getAlivePlayers()) {
                nms.hidePlayer(alive, player);
            }
            for (Player dead : arena.getDeadPlayers()) {
                nms.showPlayer(player, dead);
            }
        }
        ItemStack playAgainItem = ItemBuilder.fromConfig(Configuration.PLAY_AGAIN_ITEM.getAsConfigSection()).setTag("playagain-item").build();
        ItemStack leaveItem = ItemBuilder.fromConfig(Configuration.LEAVE_ITEM.getAsConfigSection()).setTag("leave-item").build();
        for (Player player : arena.getPlayers()) {
            player.getInventory().setItem(7, playAgainItem);
            player.getInventory().setItem(8, leaveItem);
            PlayerData data = ArenaUtil.getPlayerData(player);
            data.setGamesPlayed(data.getGamesPlayed() + 1);
        }
    }

}
