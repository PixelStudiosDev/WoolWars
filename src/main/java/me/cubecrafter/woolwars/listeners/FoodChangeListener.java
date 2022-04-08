package me.cubecrafter.woolwars.listeners;

import me.cubecrafter.woolwars.utils.GameUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodChangeListener implements Listener {

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent e) {
        Player player = (Player) e.getEntity();
        if (GameUtil.isPlaying(player)) {
            e.setCancelled(true);
        }
    }

}
