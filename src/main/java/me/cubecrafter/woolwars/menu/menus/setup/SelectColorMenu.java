package me.cubecrafter.woolwars.menu.menus.setup;

import me.cubecrafter.woolwars.team.TeamColor;
import me.cubecrafter.woolwars.arena.setup.SetupSession;
import me.cubecrafter.woolwars.arena.setup.TeamData;
import me.cubecrafter.woolwars.menu.Menu;
import me.cubecrafter.woolwars.menu.MenuItem;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SelectColorMenu extends Menu {

    private final SetupSession session;

    public SelectColorMenu(Player player, SetupSession session) {
        super(player);
        this.session = session;
    }

    @Override
    public String getTitle() {
        return "● Select Team Color";
    }

    @Override
    public int getRows() {
        return 4;
    }

    @Override
    public Map<Integer, MenuItem> getItems() {
        addFiller(new ItemBuilder("GRAY_STAINED_GLASS_PANE").setDisplayName("&f").build(), Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 28, 29, 30, 31, 32, 33, 34, 35));
        Map<Integer, MenuItem> items = new HashMap<>();
        Iterator<Integer> it = Arrays.asList(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25).iterator();
        for (TeamColor color : TeamColor.values()) {
            if (!it.hasNext()) break;
            if (session.getTeams().stream().anyMatch(team -> team.getColor() == color)) continue;
            items.put(it.next(), new MenuItem(new ItemBuilder(color.getWoolMaterial()).setDisplayName(color.getChatColor().toString() + color).setLore("&eClick to select!").build(), player).addAction(e -> {
                TeamData data = new TeamData(color);
                session.getTeams().add(data);
                new TeamSetupMenu(player, session, data).openMenu();
            }));
        }
        items.put(27, new MenuItem(new ItemBuilder("ARROW").setDisplayName("&c&lGo Back")
                .setLore("&7To all teams menu.").build(), player).addAction(e -> {
            new TeamListMenu(player, session).openMenu();
        }));
        return items;
    }

    @Override
    public boolean update() {
        return false;
    }

}
