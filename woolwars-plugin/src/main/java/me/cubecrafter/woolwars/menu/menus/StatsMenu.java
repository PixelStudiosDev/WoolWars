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
        addFiller(new ItemBuilder("GRAY_STAINED_GLASS_PANE").setDisplayName("&f").build(), Arrays.asList(0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,37,38,39,41,42,43,44));
        items.add(new MenuItem(40, new ItemBuilder("ARROW").setDisplayName("&cGo Back").setLore(Arrays.asList("&7To Main Menu")).build()).addAction(e -> {
            new MainMenu(player).openMenu();
        }).setClickSound("UI_BUTTON_CLICK"));
        items.add(new MenuItem(11, new ItemBuilder("PLAYER_HEAD").setDisplayName("&aWins").setLore(Arrays.asList(TextUtil.format("&7{wins}", player))).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDJjM2Q3MzE3NzA3OTdlMTdhYmNiMTRhZTBmYzlkOWUwNjhkZWI3NDlkZGRiOTI1MDFjMGY2MTQ1NzY5ZTI1YyJ9fX0=").build()));
        items.add(new MenuItem(12, new ItemBuilder("PLAYER_HEAD").setDisplayName("&aLosses").setLore(Arrays.asList(TextUtil.format("&7{losses}", player))).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2VmMTE5ZjA4ODUxYTcyYTVmMTBmYmMzMjQ3ZDk1ZTFjMDA2MzYwZDJiNGY0MTJiMjNjZTA1NDA5Mjc1NmIwYyJ9fX0=").build()));
        items.add(new MenuItem(13, new ItemBuilder("PLAYER_HEAD").setDisplayName("&aGames Played").setLore(Arrays.asList(TextUtil.format("&7{games_played}", player))).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzc0MTcwYzY2YmYzMTQwZjIzNGIzMjJhZGQ3MjRjNWRmNjk0OWE5MjA5ZjgwN2ViZjg2ZDRmOWM4YzFlMTc4In19fQ==").build()));
        items.add(new MenuItem(14, new ItemBuilder("PLAYER_HEAD").setDisplayName("&aKills").setLore(Arrays.asList(TextUtil.format("&7{kills}", player))).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjFmNTA1MmM3NGI3OTA1YmZmYmVmYjVjMTBiODU2NzViODk4M2VkYzllMWYyYjEwMWZmOTA2ZWI1YWMxNDQyYiJ9fX0=").build()));
        items.add(new MenuItem(15, new ItemBuilder("PLAYER_HEAD").setDisplayName("&aDeaths").setLore(Arrays.asList(TextUtil.format("&7{deaths}", player))).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzFiOTZmODhiODJmMTIzNTRhMDkzZWU3N2FiMzdiOWQyNDVkYjNmY2EwZjYwMTJjZTE1NjViZTA2NjRjODA3MiJ9fX0=").build()));
        items.add(new MenuItem(21, new ItemBuilder("PLAYER_HEAD").setDisplayName("&aPlaced Wool").setLore(Arrays.asList(TextUtil.format("&7{placed_wool}", player))).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDA4ZGY2MGM1MTA3NGVlZjI1NDRmZjM4Y2VhZDllMTY2NzVhZTQyNTE5MTYxMDUxODBlMWY4Y2UxOTdhYjNiYyJ9fX0=").build()));
        items.add(new MenuItem(22, new ItemBuilder("PLAYER_HEAD").setDisplayName("&aBroken Blocks").setLore(Arrays.asList(TextUtil.format("&7{broken_blocks}", player))).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWU0MzUyNjgwZDBiYjI5YjkxMzhhZjc4MzMwMWEzOTFiMzQwOTBjYjQ5NDFkNTJjMDg3Y2E3M2M4MDM2Y2I1MSJ9fX0=").build()));
        items.add(new MenuItem(23, new ItemBuilder("PLAYER_HEAD").setDisplayName("&aPowerups Collected").setLore(Arrays.asList(TextUtil.format("&7{powerups_collected}", player))).setTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODdkODg1YjMyYjBkZDJkNmI3ZjFiNTgyYTM0MTg2ZjhhNTM3M2M0NjU4OWEyNzM0MjMxMzJiNDQ4YjgwMzQ2MiJ9fX0=").build()));
        return items;
    }

    @Override
    public boolean update() {
        return false;
    }

}
