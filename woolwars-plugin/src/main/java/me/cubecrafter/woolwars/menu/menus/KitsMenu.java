package me.cubecrafter.woolwars.menu.menus;

import me.cubecrafter.woolwars.database.PlayerData;
import me.cubecrafter.woolwars.game.kits.Kit;
import me.cubecrafter.woolwars.menu.Menu;
import me.cubecrafter.woolwars.menu.MenuItem;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KitsMenu extends Menu {

    private final PlayerData data;

    public KitsMenu(Player player) {
        super(player);
        data = ArenaUtil.getPlayerData(player);
    }

    @Override
    public String getTitle() {
        return "● Kit Selector";
    }

    @Override
    public int getRows() {
        return 5;
    }

    @Override
    public List<MenuItem> getItems() {
        List<MenuItem> items = new ArrayList<>();
        for (Kit kit : ArenaUtil.getKits()) {
            items.add(new MenuItem(kit.getMenuSlot(), generateItem(kit)).addAction(e -> {
                kit.addToPlayer(player, ArenaUtil.getArenaByPlayer(player).getTeamByPlayer(player));
                data.setSelectedKit(kit.getId());
            }).setClickSound("UI_BUTTON_CLICK"));
        }
        return items;
    }

    private ItemStack generateItem(Kit kit) {
        ItemMeta meta = kit.getMenuItem().getItemMeta();
        if (!meta.hasLore()) return new ItemBuilder(kit.getMenuItem().clone()).setGlow(data.getSelectedKit().equals(kit.getId())).build();
        List<String> newLore = meta.getLore().stream().map(s -> s.replace("{kit_status}", data.getSelectedKit().equals(kit.getId()) ? "&aAlready Selected!" : "&eClick to Select!")).collect(Collectors.toList());
        return new ItemBuilder(kit.getMenuItem().clone()).setLore(newLore).setGlow(data.getSelectedKit().equals(kit.getId())).build();
    }

    @Override
    public boolean update() {
        return false;
    }

}
