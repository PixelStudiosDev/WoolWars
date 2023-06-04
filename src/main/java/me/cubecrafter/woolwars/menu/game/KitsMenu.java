/*
 * Wool Wars
 * Copyright (C) 2023 CubeCrafter Development
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.cubecrafter.woolwars.menu.game;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.api.events.player.PlayerSelectKitEvent;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.config.Menus;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.kit.Kit;
import me.cubecrafter.woolwars.kit.KitManager;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.xutils.Events;
import me.cubecrafter.xutils.menu.Menu;
import me.cubecrafter.xutils.menu.MenuItem;
import org.bukkit.configuration.ConfigurationSection;

public class KitsMenu extends Menu {

    private static final KitManager manager = WoolWars.get().getKitManager();

    private final WoolPlayer player;
    private final Arena arena;

    public KitsMenu(WoolPlayer player, Arena arena) {
        super(player.getPlayer());
        this.player = player;
        this.arena = arena;

        setAutoUpdate(false);
    }

    @Override
    public String getTitle() {
        return Menus.KITS_MENU_TITLE.asString();
    }

    @Override
    public int getRows() {
        return Menus.KITS_MENU_ROWS.asInt();
    }

    @Override
    public void update() {
        if (Menus.KITS_MENU_FILLER_ENABLED.asBoolean()) {
            setItem(new MenuItem(ItemBuilder.fromConfig(Menus.KITS_MENU_FILLER.asSection()).build()), Menus.KITS_MENU_FILLER_SLOTS.asIntegerList());
        }

        ConfigurationSection section = Menus.KITS_MENU_KITS_SECTION.asSection();
        for (String id : section.getKeys(false)) {
            Kit kit = manager.getKit(id);
            setItem(getKitItem(kit), section.getInt(id + ".slot"));
        }
    }


    private MenuItem getKitItem(Kit kit) {
        boolean selected = player.getSelectedKit() != null && player.getSelectedKit().equals(kit);

        ConfigurationSection section = Menus.KITS_MENU_KITS_SECTION.asSection().getConfigurationSection(kit.getId());
        ItemBuilder builder = ItemBuilder.fromConfig(section);

        builder.setGlow(selected);
        builder.addPlaceholder("{kit_status}", selected ? Messages.KIT_STATUS_SELECTED.asString() : Messages.KIT_STATUS_NOT_SELECTED.asString());

        MenuItem item = new MenuItem(builder.build());
        item.addAction(event -> {
            if (Events.call(new PlayerSelectKitEvent(player, kit, arena))) {
                return;
            }
            if (selected) {
                player.send(Messages.KIT_ALREADY_SELECTED.asString());
            } else {
                kit.addToPlayer(player, arena.getTeam(player));
            }
        });

        return item;
    }

}
