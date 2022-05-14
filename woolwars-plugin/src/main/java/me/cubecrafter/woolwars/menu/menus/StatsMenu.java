package me.cubecrafter.woolwars.menu.menus;

import me.cubecrafter.woolwars.menu.Menu;
import me.cubecrafter.woolwars.menu.MenuItem;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatsMenu extends Menu {

    public StatsMenu(Player player) {
        super(player);
    }

    @Override
    public String getTitle() {
        return "● Your Wool Wars Stats";
    }

    @Override
    public int getRows() {
        return 3;
    }

    @Override
    public List<MenuItem> getItems() {
        List<MenuItem> items = new ArrayList<>();

        items.add(new MenuItem(22, new ItemBuilder("ARROW").setDisplayName("&cGo Back").setLore(Arrays.asList("&7To Main Menu")).build()).addAction(e -> {
            new GameMenu(player).openMenu();
        }).setClickSound("UI_BUTTON_CLICK"));

        return items;
    }

    @Override
    public boolean update() {
        return false;
    }

}
