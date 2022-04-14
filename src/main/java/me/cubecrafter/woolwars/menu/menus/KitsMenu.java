package me.cubecrafter.woolwars.menu.menus;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.menu.Menu;
import me.cubecrafter.woolwars.menu.MenuItem;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class KitsMenu extends Menu {

    public KitsMenu(Player player) {
        super(player);
    }

    @Override
    public String getTitle() {
        return "Select Kit";
    }

    @Override
    public int getRows() {
        return 3;
    }

    @Override
    public List<MenuItem> getItems() {
        List<MenuItem> items = new ArrayList<>();
        Iterator<Integer> index = Arrays.asList(0,1,2,3,4,5,6,7,8).iterator();

        for (String key : WoolWars.getInstance().getFileManager().getKits().getKeys(false)) {
            items.add(new MenuItem(index.next(), new ItemBuilder("PAPER").setDisplayName(key).build()).addAction( e -> {
                player.sendMessage("hi man");
            }, ClickType.LEFT, ClickType.RIGHT).setClickSound("UI_BUTTON_CLICK"));
        }
        return items;
    }

}
