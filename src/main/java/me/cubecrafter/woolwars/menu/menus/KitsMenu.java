package me.cubecrafter.woolwars.menu.menus;

import me.cubecrafter.woolwars.kits.Kit;
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

public class KitsMenu extends Menu {

    public KitsMenu(Player player) {
        super(player);
    }

    @Override
    public String getTitle() {
        return "● Select Kit";
    }

    @Override
    public int getRows() {
        return 5;
    }

    @Override
    public List<MenuItem> getItems() {
        List<MenuItem> items = new ArrayList<>();
        Iterator<Integer> index = Arrays.asList(10,11,12,13,14,15,16).iterator();
        for (Kit kit : GameUtil.getKits()) {
            items.add(new MenuItem(index.next(), kit.getMenuItem()).addAction( e -> {
                kit.addToPlayer(player, GameUtil.getArenaByPlayer(player).getTeamByPlayer(player));
                player.sendMessage(TextUtil.color("&7Kit &b" + kit.getDisplayName() + " &7equipped!"));
                GameUtil.getArenaByPlayer(player).getPreRoundTask().getKitSelected().add(player);
                closeMenu();
            }, ClickType.LEFT, ClickType.RIGHT).setClickSound("UI_BUTTON_CLICK"));
        }
        addFiller(new ItemBuilder("GRAY_STAINED_GLASS_PANE").setDisplayName("&f").build(), Arrays.asList(0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,37,38,39,40,41,42,43,44));
        return items;
    }

}
