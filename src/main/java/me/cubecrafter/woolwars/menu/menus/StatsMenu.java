package me.cubecrafter.woolwars.menu.menus;

import me.cubecrafter.woolwars.config.Menus;
import me.cubecrafter.woolwars.menu.Menu;
import me.cubecrafter.woolwars.menu.MenuItem;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class StatsMenu extends Menu {

    public StatsMenu(Player player) {
        super(player);
    }

    @Override
    public String getTitle() {
        return Menus.STATS_MENU_TITLE.getAsString();
    }

    @Override
    public int getRows() {
        return Menus.STATS_MENU_ROWS.getAsInt();
    }

    @Override
    public Map<Integer, MenuItem> getItems() {
        if (Menus.STATS_MENU_FILLER_ENABLED.getAsBoolean()) {
            addFiller(ItemBuilder.fromConfig(Menus.STATS_MENU_FILLER.getAsConfigSection()).build(), Menus.STATS_MENU_FILLER_SLOTS.getAsIntegerList());
        }
        Map<Integer, MenuItem> items = new HashMap<>();
        items.put(Menus.STATS_MENU_WINS_ITEM_SLOT.getAsInt(), new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_WINS_ITEM.getAsConfigSection()).build(), player));
        items.put(Menus.STATS_MENU_LOSSES_ITEM_SLOT.getAsInt(), new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_LOSSES_ITEM.getAsConfigSection()).build(), player));
        items.put(Menus.STATS_MENU_GAMES_PLAYED_ITEM_SLOT.getAsInt(), new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_GAMES_PLAYED_ITEM.getAsConfigSection()).build(), player));
        items.put(Menus.STATS_MENU_KILLS_ITEM_SLOT.getAsInt(), new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_KILLS_ITEM.getAsConfigSection()).build(), player));
        items.put(Menus.STATS_MENU_DEATHS_ITEM_SLOT.getAsInt(), new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_DEATHS_ITEM.getAsConfigSection()).build(), player));
        items.put(Menus.STATS_MENU_WOOL_PLACED_ITEM_SLOT.getAsInt(), new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_WOOL_PLACED_ITEM.getAsConfigSection()).build(), player));
        items.put(Menus.STATS_MENU_BLOCKS_BROKEN_ITEM_SLOT.getAsInt(), new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_BLOCKS_BROKEN_ITEM.getAsConfigSection()).build(), player));
        items.put(Menus.STATS_MENU_POWERUPS_COLLECTED_ITEM_SLOT.getAsInt(), new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_POWERUPS_COLLECTED_ITEM.getAsConfigSection()).build(), player));
        items.put(Menus.STATS_MENU_CLOSE_ITEM_SLOT.getAsInt(), new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_CLOSE_ITEM.getAsConfigSection()).build(), player)
                .addAction((e) -> closeMenu()));
        return items;
    }

    @Override
    public boolean update() {
        return false;
    }

}
