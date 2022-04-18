package me.cubecrafter.woolwars.menu;

import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

@RequiredArgsConstructor
public abstract class Menu implements InventoryHolder {

    protected final Player player;
    private Inventory inventory;
    private BukkitTask updateTask;
    public abstract String getTitle();
    public abstract int getRows();
    public abstract List<MenuItem> getItems();
    public abstract boolean autoUpdate();

    public void openMenu() {
        updateMenu();
        player.openInventory(getInventory());
        if (autoUpdate()) {
            updateTask = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), () -> {
                if (player.getOpenInventory() == null) updateTask.cancel();
                updateMenu();
            }, 0L, 20L);
        }
    }

    public void closeMenu() {
        player.closeInventory();
    }

    public void updateMenu() {
        getItems().forEach(item -> getInventory().setItem(item.getSlot(), item.getItem()));
    }

    public void addFiller(ItemStack filler, List<Integer> slots) {
        slots.forEach(slot -> getInventory().setItem(slot, filler));
    }

    @Override
    public Inventory getInventory() {
        if (inventory == null) inventory = Bukkit.createInventory(this, getRows()*9, TextUtil.color(getTitle()));
        return inventory;
    }

}
