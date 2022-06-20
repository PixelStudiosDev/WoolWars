package me.cubecrafter.woolwars.menu.menus;

import me.cubecrafter.woolwars.arena.GameArena;
import me.cubecrafter.woolwars.config.Menus;
import me.cubecrafter.woolwars.menu.Menu;
import me.cubecrafter.woolwars.menu.MenuItem;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TeleporterMenu extends Menu {

    private final GameArena arena;

    public TeleporterMenu(Player player, GameArena arena) {
        super(player);
        this.arena = arena;
    }

    @Override
    public String getTitle() {
        return Menus.TELEPORTER_MENU_TITLE.getAsString();
    }

    @Override
    public int getRows() {
        return arena.getAlivePlayers().size() == 0 ? 1 : (int) Math.ceil(arena.getAlivePlayers().size() / 9.0);
    }

    @Override
    public Map<Integer, MenuItem> getItems() {
        Map<Integer, MenuItem> items = new HashMap<>();
        for (int i = 0; i < arena.getAlivePlayers().size(); i++) {
            Player alive = arena.getAlivePlayers().get(i);
            items.put(i, new MenuItem(generateItem(alive, ItemBuilder.fromConfig(Menus.TELEPORTER_MENU_PLAYER_ITEM.getAsConfigSection()).build()), player).addAction(e -> {
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

    private ItemStack generateItem(Player player, ItemStack original) {
        ItemMeta meta = original.getItemMeta();
        if (meta.hasDisplayName()) {
            meta.setDisplayName(meta.getDisplayName().replace("{player}", player.getName()));
        }
        if (meta.hasLore()) {
            meta.setLore(meta.getLore().stream().map(s -> s.replace("{player}", player.getDisplayName())
                    .replace("{player_health_percentage}", String.valueOf((int) (player.getHealth() / 20 * 100)))).collect(Collectors.toList()));
        }
        original.setItemMeta(meta);
        return new ItemBuilder(original).setTexture(player.getName()).build();
    }

    @Override
    public boolean update() {
        return true;
    }

}
