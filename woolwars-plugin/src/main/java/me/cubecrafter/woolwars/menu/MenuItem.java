package me.cubecrafter.woolwars.menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
    private static final ClickType[] defaultClickTypes = new ClickType[]{ClickType.LEFT, ClickType.RIGHT, ClickType.SHIFT_LEFT, ClickType.SHIFT_RIGHT};

    public MenuItem addAction(Consumer<InventoryClickEvent> action, ClickType... clickTypes) {
        if (clickTypes.length == 0) {
            clickTypes = defaultClickTypes;
        }
        actions.put(action, clickTypes);
        return this;
    }

    public MenuItem setClickSound(String sound) {
        this.clickSound = sound;
        return this;
    }

}
