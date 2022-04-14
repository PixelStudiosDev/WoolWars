package me.cubecrafter.woolwars.listeners;

import me.cubecrafter.woolwars.menu.menus.KitsMenu;
import me.cubecrafter.woolwars.utils.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getItem() == null || e.getItem().getType().equals(Material.AIR)) return;
        Player player = e.getPlayer();
        if (ItemUtil.hasId(e.getItem(), "kit-item")) {
            new KitsMenu(player).openMenu();
        }
    }

}
