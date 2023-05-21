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

import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.config.Menus;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.xutils.menu.Menu;
import me.cubecrafter.xutils.menu.MenuItem;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class TeleporterMenu extends Menu {

    private final Arena arena;
    private final Player player;

    public TeleporterMenu(WoolPlayer player, Arena arena) {
        super(player.getPlayer());
        this.player = player.getPlayer();
        this.arena = arena;
    }

    @Override
    public String getTitle() {
        return Menus.TELEPORTER_MENU_TITLE.asString();
    }

    @Override
    public int getRows() {
        if (arena.getAlivePlayers().size() == 0) {
            return 1;
        }
        return (int) Math.ceil(arena.getAlivePlayers().size() / 9.0);
    }

    @Override
    public void update() {
        for (int i = 0; i < arena.getAlivePlayers().size(); i++) {
            Player alive = arena.getAlivePlayers().get(i).getPlayer();
            setItem(getPlayerItem(alive), i);
        }
    }

    private MenuItem getPlayerItem(Player player) {
        ItemBuilder builder = ItemBuilder.fromConfig(Menus.TELEPORTER_MENU_PLAYER_ITEM.asSection());

        builder.setSkullTexture(player.getName());
        builder.addPlaceholder("{player}", player.getName());
        builder.addPlaceholder("{player_health_percentage}", String.valueOf((int) (player.getHealth() / 20 * 100)));

        MenuItem item = new MenuItem(builder.build());
        // Teleport to player
        item.addAction(event -> {
            this.player.teleport(player);
            close();
        }, ClickType.LEFT, ClickType.SHIFT_LEFT);
        // Enter spectator mode
        item.addAction(event -> {
            this.player.setGameMode(GameMode.SPECTATOR);
            this.player.setSpectatorTarget(player);
            close();
        }, ClickType.RIGHT, ClickType.SHIFT_RIGHT);

        return item;
    }

}
