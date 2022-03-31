package me.cubecrafter.woolwars.listeners;

import com.cryptomorin.xseries.XSound;
import me.cubecrafter.woolwars.menu.Menu;
import me.cubecrafter.woolwars.menu.MenuItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;
import java.util.function.Consumer;

public class MenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)) return;
        if (e.getInventory().getHolder() instanceof Menu) {
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();
            Menu menu = (Menu) e.getInventory().getHolder();
            MenuItem item = menu.getMenuItem(e.getRawSlot());
            if (item == null || item.getClickActions() == null) return;
            for (Map.Entry<Consumer<InventoryClickEvent>, ClickType[]> entry : item.getClickActions().entrySet()) {
                for (ClickType clickType : entry.getValue())
                    if (e.getClick().equals(clickType)) {
                        if (item.getClickSound() != null) {
                            XSound.play(player, item.getClickSound());
                        }
                        entry.getKey().accept(e);
                        menu.updateMenu();
                    }
                }
            }

    }

}
