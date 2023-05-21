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

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.api.events.arena.RoundStartEvent;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.kit.Kit;
import me.cubecrafter.woolwars.kit.KitManager;
import me.cubecrafter.woolwars.powerup.PowerUp;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.woolwars.arena.team.Team;
import me.cubecrafter.xutils.Events;
import me.cubecrafter.xutils.Tasks;
import me.cubecrafter.xutils.TextUtil;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;

public class PreRoundTask extends ArenaTask {

    private static final KitManager kitManager = WoolWars.get().getKitManager();

    public PreRoundTask(Arena arena) {
        super(arena, Config.PRE_ROUND_DURATION.asInt());
    }

    @Override
    public void start() {
        arena.setRound(arena.getRound() + 1);

        arena.sendTitle(Messages.PRE_ROUND_TITLE.asString(), Messages.PRE_ROUND_SUBTITLE.asString(), 2);
        arena.killEntities();
        arena.clearBlocks();
        arena.fillCenter();
        arena.getTeams().forEach(Team::newRound);

        for (WoolPlayer player : arena.getPlayers()) {
            player.reset(GameMode.SURVIVAL);
            player.setAbilityUsed(false);
            arena.getPlayers().forEach(other -> player.setVisibility(other, true));
            // Give previous kit if available, otherwise give random kit
            Kit kit = player.getSelectedKit() == null ? kitManager.getRandomKit() : player.getSelectedKit();
            kit.addToPlayer(player, arena.getTeam(player));
            // Update selected kit
            player.getData().setSelectedKit(kit.getId());

            TextUtil.sendActionBarWhile(player.getPlayer(), Messages.SHIFT_TO_SELECT_KIT.asString(), () -> arena.getState() == GameState.PRE_ROUND);
        }
    }

    @Override
    public void execute() {
        if (arena.getTimer() <= 3) {
            ConfigurationSection section = Messages.ROUND_START_COUNTDOWN_SECONDS.asSection();
            String[] numbers = new String[] { section.getString("one"), section.getString("two"), section.getString("three") };
            arena.sendTitle(Messages.ROUND_START_COUNTDOWN_TITLE.asString().replace("{seconds}", numbers[arena.getTimer() - 1]), Messages.ROUND_START_COUNTDOWN_SUBTITLE.asString(), 1);
            arena.playSound(Config.SOUNDS_COUNTDOWN.asString());
        }
    }

    @Override
    public GameState end() {
        Events.call(new RoundStartEvent(arena, arena.getRound()));

        arena.getPlayers().forEach(player -> player.getPlayer().closeInventory());
        arena.sendTitle(Messages.ROUND_START_TITLE.asString(), Messages.ROUND_START_SUBTITLE.asString().replace("{round}", String.valueOf(arena.getRound())), 1);
        arena.getTeams().forEach(Team::removeBarrier);
        arena.getPowerUps().forEach(PowerUp::spawn);

        Tasks.repeatTimes(() -> arena.playSound(Config.SOUNDS_ROUND_START.asString()), 0, 3, 3);

        return GameState.ACTIVE_ROUND;
    }

}
