package me.cubecrafter.woolwars.menu;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MenuItem {

    private final ItemStack item;
    private final Map<Consumer<InventoryClickEvent>, ClickType[]> actions = new HashMap<>();
    private String sound;

    public MenuItem(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    public MenuItem addAction(Consumer<InventoryClickEvent> action, ClickType... clickTypes) {
        actions.put(action, clickTypes);
        return this;
    }

    public MenuItem setClickSound(String sound) {
        this.sound = sound;
        return this;
    }

    public Map<Consumer<InventoryClickEvent>, ClickType[]> getClickActions() {
        return actions;
    }

    public String getClickSound() {
        return sound;
    }

}
