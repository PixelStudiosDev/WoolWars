package me.cubecrafter.woolwars.game.listeners;

import me.cubecrafter.woolwars.menu.menus.KitsMenu;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (ItemBuilder.hasTag(e.getItem(), "kit-item")) {
            new KitsMenu(player).openMenu();
        }
    }

}
