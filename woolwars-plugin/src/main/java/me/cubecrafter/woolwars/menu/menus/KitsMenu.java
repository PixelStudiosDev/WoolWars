package me.cubecrafter.woolwars.menu.menus;

import me.cubecrafter.woolwars.api.database.PlayerData;
import me.cubecrafter.woolwars.kits.Kit;
import me.cubecrafter.woolwars.menu.Menu;
import me.cubecrafter.woolwars.menu.MenuItem;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public Map<Integer, MenuItem> getItems() {
        Map<Integer, MenuItem> items = new HashMap<>();
        for (Kit kit : ArenaUtil.getKits()) {
            items.put(kit.getMenuSlot(), new MenuItem(generateItem(kit), player).addAction(e -> {
                if (data.getSelectedKit().equals(kit.getId())) {
                    TextUtil.sendMessage(player, "&cYou already have this kit selected!");
                } else {
                    kit.addToPlayer(player, ArenaUtil.getArenaByPlayer(player).getTeamByPlayer(player));
                    data.setSelectedKit(kit.getId());
                }
            }));
        }
        return items;
    }

    private ItemStack generateItem(Kit kit) {
        ItemMeta meta = kit.getMenuItem().getItemMeta();
        if (!meta.hasLore()) return new ItemBuilder(kit.getMenuItem().clone()).setGlow(data.getSelectedKit().equals(kit.getId())).build();
        List<String> newLore = meta.getLore().stream().map(s -> s.replace("{kit_status}", data.getSelectedKit().equals(kit.getId()) ? "&cAlready Selected!" : "&eClick to Select!")).collect(Collectors.toList());
        return new ItemBuilder(kit.getMenuItem().clone()).setLore(newLore).setGlow(data.getSelectedKit().equals(kit.getId())).build();
    }

    @Override
    public boolean update() {
        return false;
    }

}
