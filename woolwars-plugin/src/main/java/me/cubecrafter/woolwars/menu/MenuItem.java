package me.cubecrafter.woolwars.menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
public class MenuItem {

    private final int slot;
    private final ItemStack item;
    private final Map<Consumer<InventoryClickEvent>, ClickType[]> actions = new HashMap<>();
    private String clickSound;

    public MenuItem addAction(Consumer<InventoryClickEvent> action, ClickType... clickTypes) {
        actions.put(action, clickTypes);
        return this;
    }

    public MenuItem setClickSound(String sound) {
        this.clickSound = sound;
        return this;
    }

}
