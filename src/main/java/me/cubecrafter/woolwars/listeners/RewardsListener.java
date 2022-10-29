/*
 * Wool Wars
 * Copyright (C) 2022 CubeCrafter Development
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
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class RewardsListener implements Listener {

    @EventHandler
    public void onRoundEnd(RoundEndEvent e) {
        for (Team team : e.getLoserTeams()) {
            team.getMembers().forEach(player -> executeCommands(player, Config.REWARD_COMMANDS_ROUND_LOSE.getAsStringList()));
        }
        e.getWinnerTeam().getMembers().forEach(player -> executeCommands(player, Config.REWARD_COMMANDS_ROUND_WIN.getAsStringList()));
    }

    @EventHandler
    public void onKillDeath(PlayerKillEvent e) {
        executeCommands(e.getVictim(), Config.REWARD_COMMANDS_DEATH.getAsStringList());
        if (e.getAttacker() == null) return;
        executeCommands(e.getAttacker(), Config.REWARD_COMMANDS_KILL.getAsStringList());
    }

    @EventHandler
    public void onGameEnd(GameEndEvent e) {
        for (Team team : e.getLoserTeams()) {
            team.getMembers().forEach(player -> executeCommands(player, Config.REWARD_COMMANDS_MATCH_LOSE.getAsStringList()));
        }
        e.getWinnerTeam().getMembers().forEach(player -> executeCommands(player, Config.REWARD_COMMANDS_MATCH_WIN.getAsStringList()));
    }

    public void executeCommands(Player player, List<String> commands) {
        for (String command : commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName()).replace("{uuid}", player.getUniqueId().toString()));
        }
    }

}
