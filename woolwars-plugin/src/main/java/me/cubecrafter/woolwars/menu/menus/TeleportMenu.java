package me.cubecrafter.woolwars.menu.menus;

import me.cubecrafter.woolwars.game.arena.Arena;
import me.cubecrafter.woolwars.menu.Menu;
import me.cubecrafter.woolwars.menu.MenuItem;
import me.cubecrafter.woolwars.utils.GameUtil;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TeleportMenu extends Menu {

    private final Arena arena;

    public TeleportMenu(Player player) {
        super(player);
        arena = GameUtil.getArenaByPlayer(player);
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
    public List<MenuItem> getItems() {
        List<MenuItem> items = new ArrayList<>();

        for (int i = 0; i < arena.getAlivePlayers().size(); i++) {
            Player alive = arena.getAlivePlayers().get(i);
            items.add(new MenuItem(i, new ItemBuilder("PLAYER_HEAD").setDisplayName(alive.getDisplayName()).setLore(Arrays.asList("&7Left click to teleport!", "&7Right click to spectate!")).setTexture(player.getName()).build()).addAction(e -> {
                closeMenu();
                player.teleport(alive);
            }, ClickType.LEFT).addAction(e -> {
                closeMenu();
                player.setGameMode(GameMode.SPECTATOR);
                player.setSpectatorTarget(alive);
            }, ClickType.RIGHT).setClickSound("ENTITY_SLIME_WALK"));
        }

        return items;
    }

    @Override
    public boolean update() {
        return true;
    }

}
