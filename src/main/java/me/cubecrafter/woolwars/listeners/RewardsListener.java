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

package me.cubecrafter.woolwars.listeners;

import me.cubecrafter.woolwars.api.events.arena.GameEndEvent;
import me.cubecrafter.woolwars.api.events.arena.RoundEndEvent;
import me.cubecrafter.woolwars.api.events.player.PlayerKillEvent;
import me.cubecrafter.woolwars.arena.team.Team;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class RewardsListener implements Listener {

    @EventHandler
    public void onRoundEnd(RoundEndEvent event) {
        if (!Config.REWARD_COMMANDS_ENABLED.asBoolean()) return;

        for (Team team : event.getLoserTeams()) {
            team.getMembers().forEach(player -> executeCommands(player, Config.REWARD_COMMANDS_ROUND_LOSE.asStringList()));
        }
        if (event.getWinnerTeam() == null) return;
        event.getWinnerTeam().getMembers().forEach(player -> executeCommands(player, Config.REWARD_COMMANDS_ROUND_WIN.asStringList()));
    }

    @EventHandler
    public void onPlayerKill(PlayerKillEvent event) {
        if (!Config.REWARD_COMMANDS_ENABLED.asBoolean()) return;

        executeCommands(event.getVictim(), Config.REWARD_COMMANDS_DEATH.asStringList());
        if (event.getAttacker() == null) return;
        executeCommands(event.getAttacker(), Config.REWARD_COMMANDS_KILL.asStringList());
    }

    @EventHandler
    public void onGameEnd(GameEndEvent event) {
        if (!Config.REWARD_COMMANDS_ENABLED.asBoolean()) return;

        for (Team team : event.getLoserTeams()) {
            team.getMembers().forEach(player -> executeCommands(player, Config.REWARD_COMMANDS_MATCH_LOSE.asStringList()));
        }
        event.getWinnerTeam().getMembers().forEach(player -> executeCommands(player, Config.REWARD_COMMANDS_MATCH_WIN.asStringList()));
    }

    public void executeCommands(WoolPlayer player, List<String> commands) {
        for (String command : commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName()).replace("{uuid}", player.getUniqueId().toString()));
        }
    }

}
