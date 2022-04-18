package me.cubecrafter.woolwars.menu.menus;

import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.menu.Menu;
import me.cubecrafter.woolwars.menu.MenuItem;
import me.cubecrafter.woolwars.utils.GameUtil;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ArenaListMenu extends Menu {

    public ArenaListMenu(Player player) {
        super(player);
    }

    @Override
    public String getTitle() {
        return "● Wool Wars Arenas";
    }

    @Override
    public int getRows() {
        return 5;
    }

    @Override
    public List<MenuItem> getItems() {
        List<MenuItem> items = new ArrayList<>();

        Iterator<Integer> index = Arrays.asList(10,11,12,13,14,15,16).iterator();

        for (Arena arena : GameUtil.getArenas()) {
            if (index.hasNext()) {
                items.add(new MenuItem(index.next(), new ItemBuilder("PAPER").setDisplayName(arena.getDisplayName())
                        .setLore(Arrays.asList(TextUtil.parsePlaceholders("Players: {players}/{max_players}", arena), TextUtil.parsePlaceholders("Group: {group}", arena))).build()).addAction(e -> {
                    closeMenu();
                    arena.addPlayer(player);
                }, ClickType.LEFT, ClickType.SHIFT_LEFT, ClickType.RIGHT, ClickType.SHIFT_RIGHT));
            }
        }

        // ARENA GROUP SORTING

        addFiller(new ItemBuilder("GRAY_STAINED_GLASS_PANE").setDisplayName("&f").build(), Arrays.asList(0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,37,38,39,40,41,42,43,44));

        return items;
    }

    @Override
    public boolean autoUpdate() {
        return true;
    }

}
