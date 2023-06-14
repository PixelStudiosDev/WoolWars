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

import me.cubecrafter.woolwars.config.Menus;
import me.cubecrafter.woolwars.storage.player.PlayerData;
import me.cubecrafter.woolwars.storage.player.StatisticType;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.xutils.menu.Menu;
import me.cubecrafter.xutils.menu.MenuItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class StatsMenu extends Menu {

    private final PlayerData data;

    public StatsMenu(WoolPlayer player, PlayerData data) {
        super(player.getPlayer());
        this.data = data;

        setAutoUpdate(false);
    }

    @Override
    public String getTitle() {
        OfflinePlayer player = Bukkit.getOfflinePlayer(data.getUuid());
        return Menus.STATS_MENU_TITLE.asString().replace("{player}", player.getName());
    }

    @Override
    public int getRows() {
        return Menus.STATS_MENU_ROWS.asInt();
    }

    @Override
    public void update() {
        if (Menus.STATS_MENU_FILLER_ENABLED.asBoolean()) {
            MenuItem filler = new MenuItem(ItemBuilder.fromConfig((Menus.STATS_MENU_FILLER.asSection())).build());
            setItem(filler, Menus.STATS_MENU_FILLER_SLOTS.asIntegerList());
        }

        setItem(new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_WINS_ITEM.asSection())
                .addPlaceholder("{wins}", String.valueOf(data.getStatistic(StatisticType.WINS))).build()), Menus.STATS_MENU_WINS_ITEM_SLOT.asInt());

        setItem(new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_LOSSES_ITEM.asSection())
                .addPlaceholder("{losses}", String.valueOf(data.getStatistic(StatisticType.LOSSES))).build()), Menus.STATS_MENU_LOSSES_ITEM_SLOT.asInt());

        setItem(new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_GAMES_PLAYED_ITEM.asSection())
                .addPlaceholder("{games_played}", String.valueOf(data.getStatistic(StatisticType.GAMES_PLAYED))).build()), Menus.STATS_MENU_GAMES_PLAYED_ITEM_SLOT.asInt());

        setItem(new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_KILLS_ITEM.asSection())
                .addPlaceholder("{kills}", String.valueOf(data.getStatistic(StatisticType.KILLS))).build()), Menus.STATS_MENU_KILLS_ITEM_SLOT.asInt());

        setItem(new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_DEATHS_ITEM.asSection())
                .addPlaceholder("{deaths}", String.valueOf(data.getStatistic(StatisticType.DEATHS))).build()), Menus.STATS_MENU_DEATHS_ITEM_SLOT.asInt());

        setItem(new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_WOOL_PLACED_ITEM.asSection())
                .addPlaceholder("{wool_placed}", String.valueOf(data.getStatistic(StatisticType.WOOL_PLACED))).build()), Menus.STATS_MENU_WOOL_PLACED_ITEM_SLOT.asInt());

        setItem(new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_BLOCKS_BROKEN_ITEM.asSection())
                .addPlaceholder("{blocks_broken}", String.valueOf(data.getStatistic(StatisticType.BLOCKS_BROKEN))).build()), Menus.STATS_MENU_BLOCKS_BROKEN_ITEM_SLOT.asInt());

        setItem(new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_POWERUPS_COLLECTED_ITEM.asSection())
                .addPlaceholder("{powerups_collected}", String.valueOf(data.getStatistic(StatisticType.POWERUPS_COLLECTED))).build()), Menus.STATS_MENU_POWERUPS_COLLECTED_ITEM_SLOT.asInt());

        setItem(new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_WIN_STREAK_ITEM.asSection())
                .addPlaceholder("{win_streak}", String.valueOf(data.getStatistic(StatisticType.WIN_STREAK))).build()), Menus.STATS_MENU_WIN_STREAK_ITEM_SLOT.asInt());

        setItem(new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_HIGHEST_WIN_STREAK_ITEM.asSection())
                .addPlaceholder("{highest_win_streak}", String.valueOf(data.getStatistic(StatisticType.HIGHEST_WIN_STREAK))).build()), Menus.STATS_MENU_HIGHEST_WIN_STREAK_ITEM_SLOT.asInt());

        setItem(new MenuItem(ItemBuilder.fromConfig(Menus.STATS_MENU_CLOSE_ITEM.asSection()).build()).addAction(event -> close()), Menus.STATS_MENU_CLOSE_ITEM_SLOT.asInt());
    }

}
