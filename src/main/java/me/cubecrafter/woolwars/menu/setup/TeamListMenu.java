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

package me.cubecrafter.woolwars.menu.setup;

import me.cubecrafter.woolwars.arena.setup.SetupSession;
import me.cubecrafter.woolwars.arena.setup.TeamData;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.xutils.menu.Menu;
import me.cubecrafter.xutils.menu.MenuItem;

import java.util.Arrays;
import java.util.Iterator;

public class TeamListMenu extends Menu {

    private final WoolPlayer player;
    private final SetupSession session;

    public TeamListMenu(WoolPlayer player, SetupSession session) {
        super(player.getPlayer());
        this.player = player;
        this.session = session;

        setAutoUpdate(false);
    }

    @Override
    public String getTitle() {
        return "● Team List";
    }

    @Override
    public int getRows() {
        return 4;
    }

    @Override
    public void update() {
        setItem(new MenuItem(new ItemBuilder("GRAY_STAINED_GLASS_PANE").setDisplayName("&f").build()),
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 28, 29, 30, 31, 32, 33, 34);

        Iterator<Integer> slots = Arrays.asList(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25).iterator();
        for (TeamData data : session.getTeams()) {
            if (!slots.hasNext()) break;

            setItem(new MenuItem(new ItemBuilder(data.getColor().getWoolMaterial()).setDisplayName(data.getColor().getChatColor().toString() + data.getColor().toString()).setLore("&8Left-Click to open settings.").build()).addAction(e -> {
                new TeamSetupMenu(player, session, data).open();
            }), slots.next());
        }

        setItem(new MenuItem(new ItemBuilder("ARROW").setDisplayName("&c&lGo Back")
                .setLore("&7To the main setup menu.").build()).addAction(e -> {
            session.getMenu().open();
        }), 27);

        setItem(new MenuItem(new ItemBuilder("EMERALD_BLOCK").setDisplayName("&a&lCreate Team")
                .setLore("&7Click to create a new team!").build()).addAction(e -> {
            new SelectColorMenu(player, session).open();
        }), 35);
    }

}
