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

package me.cubecrafter.woolwars.arena.tasks;

import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.powerup.PowerUp;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GameEndTask extends ArenaTask {

    public GameEndTask(Arena arena) {
        super(arena, Config.GAME_END_DURATION.asInt());
    }

    @Override
    public void start() {
        arena.getPowerUps().forEach(PowerUp::remove);

        for (WoolPlayer player : arena.getAlivePlayers()) {
            player.reset(GameMode.ADVENTURE);
            player.getPlayer().setAllowFlight(true);
            player.getPlayer().setFlying(true);
            player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
        }

        ItemStack playAgainItem = ItemBuilder.fromConfig(Config.PLAY_AGAIN_ITEM.asSection()).setTag("playagain").build();
        ItemStack leaveItem = ItemBuilder.fromConfig(Config.LEAVE_ITEM.asSection()).setTag("leave").build();

        for (WoolPlayer player : arena.getPlayers()) {
            player.getPlayer().getInventory().setItem(7, playAgainItem);
            player.getPlayer().getInventory().setItem(8, leaveItem);

            arena.getPlayers().forEach(other -> player.setVisibility(other, false));
        }
    }

    @Override
    public GameState end() {
        arena.restart();
        return null;
    }

}
