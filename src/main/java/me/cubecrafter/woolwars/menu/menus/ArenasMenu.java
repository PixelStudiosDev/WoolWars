package me.cubecrafter.woolwars.menu.menus;

import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.config.Menus;
import me.cubecrafter.woolwars.menu.Menu;
import me.cubecrafter.woolwars.menu.MenuItem;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.entity.Player;

import java.util.*;

public class ArenasMenu extends Menu {

    public ArenasMenu(Player player) {
        super(player);
    }

    @Override
    public String getTitle() {
        return Menus.ARENAS_MENU_TITLE.getAsString();
    }

    @Override
    public int getRows() {
        return Menus.ARENAS_MENU_ROWS.getAsInt();
    }

    @Override
    public Map<Integer, MenuItem> getItems() {
        if (Menus.ARENAS_MENU_FILLER_ENABLED.getAsBoolean()) {
            addFiller(ItemBuilder.fromConfig(Menus.ARENAS_MENU_FILLER.getAsSection()).build(), Menus.ARENAS_MENU_FILLER_SLOTS.getAsIntegerList());
        }
        Map<Integer, MenuItem> items = new HashMap<>();
        Iterator<Integer> index = Menus.ARENAS_MENU_ARENA_ITEM_SLOTS.getAsIntegerList().iterator();
        for (Arena arena : ArenaUtil.getArenas()) {
            if (!index.hasNext()) break;
            items.put(index.next(), new MenuItem(ItemBuilder.fromConfig(Menus.ARENAS_MENU_ARENA_ITEM.getAsSection())
                    .setDisplayName(TextUtil.format(Menus.ARENAS_MENU_ARENA_ITEM_DISPLAYNAME.getAsString(), arena, player))
                    .setLore(TextUtil.format(Menus.ARENAS_MENU_ARENA_ITEM_LORE.getAsStringList(), arena, player)).build(), player)
                    .addAction(e -> {
                        closeMenu();
                        arena.addPlayer(player);
                    }));
            }
        items.put(Menus.ARENAS_MENU_CLOSE_ITEM_SLOT.getAsInt(), new MenuItem(ItemBuilder.fromConfig(Menus.ARENAS_MENU_CLOSE_ITEM.getAsSection()).build(), player)
                .addAction(e -> closeMenu()));
        return items;
    }

    @Override
    public boolean update() {
        return true;
    }

}
