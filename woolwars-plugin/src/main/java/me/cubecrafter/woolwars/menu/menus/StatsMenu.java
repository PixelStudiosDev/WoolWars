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
        return 5;
    }

    @Override
    public List<MenuItem> getItems() {
        List<MenuItem> items = new ArrayList<>();
        items.add(new MenuItem(40, new ItemBuilder("ARROW").setDisplayName("&cGo Back").setLore(Arrays.asList("&7To Main Menu")).build()).addAction(e -> {
            new GameMenu(player).openMenu();
        }).setClickSound("UI_BUTTON_CLICK"));
        items.add(new MenuItem(11, new ItemBuilder("PLAYER_HEAD").setDisplayName("&aWins").setLore(Arrays.asList(TextUtil.format("{wins}", player))).build()));
        items.add(new MenuItem(12, new ItemBuilder("PLAYER_HEAD").setDisplayName("&aLosses").setLore(Arrays.asList(TextUtil.format("{losses}", player))).build()));
        items.add(new MenuItem(13, new ItemBuilder("PLAYER_HEAD").setDisplayName("&aGames Played").setLore(Arrays.asList(TextUtil.format("{games_played}", player))).build()));
        items.add(new MenuItem(14, new ItemBuilder("PLAYER_HEAD").setDisplayName("&aKills").setLore(Arrays.asList(TextUtil.format("{kills}", player))).build()));
        items.add(new MenuItem(15, new ItemBuilder("PLAYER_HEAD").setDisplayName("&aDeaths").setLore(Arrays.asList(TextUtil.format("{deaths}", player))).build()));
        items.add(new MenuItem(21, new ItemBuilder("PLAYER_HEAD").setDisplayName("&aPlaced Wool").setLore(Arrays.asList(TextUtil.format("{placed_wool}", player))).build()));
        items.add(new MenuItem(22, new ItemBuilder("PLAYER_HEAD").setDisplayName("&aBroken Blocks").setLore(Arrays.asList(TextUtil.format("{broken_blocks}", player))).build()));
        items.add(new MenuItem(23, new ItemBuilder("PLAYER_HEAD").setDisplayName("&aPowerups Collected").setLore(Arrays.asList(TextUtil.format("{powerups_collected}", player))).build()));
        return items;
    }

    @Override
    public boolean update() {
        return false;
    }

}
