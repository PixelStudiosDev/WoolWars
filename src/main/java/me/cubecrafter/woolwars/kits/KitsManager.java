package me.cubecrafter.woolwars.kits;

import me.cubecrafter.woolwars.core.Arena;
import me.cubecrafter.woolwars.utils.GameUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * @author Infinity
 * 14-04-2022 / 03:17 PM
 * WoolWars / me.cubecrafter.woolwars.kits
 */

public class KitsManager {

    public void setupInventory(Player player){
        if (GameUtil.isPlaying(player)){
            Arena arena = GameUtil.getArenaByPlayer(player);
            switch (arena.getGameState()){
                //do shit
            }
        }
    }
}
