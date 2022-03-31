package me.cubecrafter.woolwars.menu;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;

public abstract class Menu implements InventoryHolder {

    protected final YamlConfiguration messages = WoolWars.getInstance().getFileManager().getMessages();
    protected final YamlConfiguration config = WoolWars.getInstance().getFileManager().getConfig();
    protected final Player player;
    private Inventory inventory;
    private final Map<Integer, MenuItem> items = new HashMap<>();
    public abstract String getTitle();
    public abstract int getRows();
    public abstract void setItems();

    public Menu(Player player) {
        this.player = player;
    }

    public void openMenu() {
        setItems();
        player.openInventory(getInventory());
    }

    public void closeMenu() {
        player.closeInventory();
    }

    public void addMenuItem(int slot, MenuItem menuItem) {
        items.put(slot, menuItem);
        getInventory().setItem(slot, menuItem.getItem());
    }

    public MenuItem getMenuItem(int slot) {
        return items.get(slot);
    }

    public void updateMenu() {
        setItems();
    }

    @Override
    public Inventory getInventory() {
        if (inventory == null) inventory = Bukkit.createInventory(this, getRows()*9, TextUtil.color(getTitle()));
        return inventory;
    }

}
