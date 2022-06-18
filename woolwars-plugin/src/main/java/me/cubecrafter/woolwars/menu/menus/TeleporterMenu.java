package me.cubecrafter.woolwars.menu.menus;

import me.cubecrafter.woolwars.arena.GameArena;
import me.cubecrafter.woolwars.menu.Menu;
import me.cubecrafter.woolwars.menu.MenuItem;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

public class TeleporterMenu extends Menu {

    private final GameArena arena;

    public TeleporterMenu(Player player, GameArena arena) {
        super(player);
        this.arena = arena;
    }

    @Override
    public String getTitle() {
        return "● Teleport Menu";
    }

    @Override
    public int getRows() {
        return 3;
    }

    @Override
    public Map<Integer, MenuItem> getItems() {
        Map<Integer, MenuItem> items = new HashMap<>();
        for (int i = 0; i < arena.getAlivePlayers().size(); i++) {
            Player alive = arena.getAlivePlayers().get(i);
            items.put(i, new MenuItem(new ItemBuilder("PLAYER_HEAD").setDisplayName(alive.getDisplayName()).setLore(Arrays.asList("&7Left click to teleport!", "&7Right click to spectate!")).setTexture(alive.getDisplayName()).build(), player).addAction(e -> {
                closeMenu();
                player.teleport(alive);
            }, ClickType.LEFT, ClickType.SHIFT_LEFT).addAction(e -> {
                closeMenu();
                player.setGameMode(GameMode.SPECTATOR);
                player.setSpectatorTarget(alive);
            }, ClickType.RIGHT, ClickType.SHIFT_RIGHT));
        }
        return items;
    }

    @Override
    public boolean update() {
        return true;
    }

}
