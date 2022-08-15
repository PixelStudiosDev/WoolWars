package me.cubecrafter.woolwars.menu.menus;

import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.database.PlayerData;
import me.cubecrafter.woolwars.api.events.player.PlayerSelectKitEvent;
import me.cubecrafter.woolwars.config.Menus;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.kits.Kit;
import me.cubecrafter.woolwars.menu.Menu;
import me.cubecrafter.woolwars.menu.MenuItem;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KitsMenu extends Menu {

    private final PlayerData data;
    private final Arena arena;

    public KitsMenu(Player player, Arena arena) {
        super(player);
        this.arena = arena;
        data = ArenaUtil.getPlayerData(player);
    }

    @Override
    public String getTitle() {
        return Menus.KITS_MENU_TITLE.getAsString();
    }

    @Override
    public int getRows() {
        return Menus.KITS_MENU_ROWS.getAsInt();
    }

    @Override
    public Map<Integer, MenuItem> getItems() {
        if (Menus.KITS_MENU_FILLER_ENABLED.getAsBoolean()) {
            addFiller(ItemBuilder.fromConfig(Menus.KITS_MENU_FILLER.getAsConfigSection()).build(), Menus.KITS_MENU_FILLER_SLOTS.getAsIntegerList());
        }
        Map<Integer, MenuItem> items = new HashMap<>();
        ConfigurationSection kits = Menus.KITS_MENU_KITS_SECTION.getAsConfigSection();
        for (String id : kits.getKeys(false)) {
            Kit kit = ArenaUtil.getKit(id);
            items.put(kits.getInt(id + ".slot"), new MenuItem(generateItem(kit, ItemBuilder.fromConfig(kits.getConfigurationSection(id)).build()), player).addAction(e -> {
                PlayerSelectKitEvent event = new PlayerSelectKitEvent(player, kit, arena);
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled()) return;
                if (data.getSelectedKit().equals(id)) {
                    TextUtil.sendMessage(player,  Messages.KIT_ALREADY_SELECTED.getAsString());
                } else {
                    kit.addToPlayer(player, arena.getTeamByPlayer(player));
                    data.setSelectedKit(id);
                }
            }));
        }
        return items;
    }

    private ItemStack generateItem(Kit kit, ItemStack original) {
        ItemMeta meta = original.getItemMeta();
        if (!meta.hasLore()) return new ItemBuilder(original).setGlow(data.getSelectedKit().equals(kit.getId())).build();
        List<String> newLore = meta.getLore().stream().map(s -> s.replace("{kit_status}", data.getSelectedKit().equals(kit.getId()) ? Messages.KIT_STATUS_SELECTED.getAsString() : Messages.KIT_STATUS_NOT_SELECTED.getAsString())).collect(Collectors.toList());
        return new ItemBuilder(original).setLore(newLore).setGlow(data.getSelectedKit().equals(kit.getId())).build();
    }

    @Override
    public boolean update() {
        return false;
    }

}
