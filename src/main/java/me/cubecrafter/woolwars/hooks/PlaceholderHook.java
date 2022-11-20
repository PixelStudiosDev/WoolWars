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

package me.cubecrafter.woolwars.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.storage.PlayerData;
import me.cubecrafter.woolwars.team.Team;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import org.bukkit.entity.Player;

public class PlaceholderHook extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "woolwars";
    }

    @Override
    public String getAuthor() {
        return "CubeCrafter";
    }

    @Override
    public String getVersion() {
        return WoolWars.getInstance().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        String[] args = params.split("_");
        if (args[0].equals("count")) {
            if (args[1].equals("total")) {
                return String.valueOf(ArenaUtil.getArenas().stream().mapToInt(arena -> arena.getPlayers().size()).sum());
            }
            return String.valueOf(ArenaUtil.getArenasByGroup(args[1]).stream().mapToInt(arena -> arena.getPlayers().size()).sum());
        }
        if (player == null) return null;
        PlayerData data = ArenaUtil.getPlayerData(player);
        switch (params) {
            case "wins":
                return String.valueOf(data.getWins());
            case "losses":
                return String.valueOf(data.getLosses());
            case "games_played":
                return String.valueOf(data.getGamesPlayed());
            case "kills":
                return String.valueOf(data.getKills());
            case "deaths":
                return String.valueOf(data.getDeaths());
            case "wool_placed":
                return String.valueOf(data.getWoolPlaced());
            case "blocks_broken":
                return String.valueOf(data.getBlocksBroken());
            case "powerups_collected":
                return String.valueOf(data.getPowerUpsCollected());
            case "selected_kit":
                return data.getSelectedKit();
            case "win_streak":
                return String.valueOf(data.getWinStreak());
            case "highest_win_streak":
                return String.valueOf(data.getHighestWinStreak());
            case "kdr":
                if (data.getDeaths() == 0) return data.getKills() + ".00";
                return String.format("%.2f", (double) data.getKills() / data.getDeaths());
        }
        if (!params.startsWith("arena_")) return null;
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return null;
        Team team = arena.getTeamByPlayer(player);
        switch (params.substring(6)) {
            case "id":
                return arena.getId();
            case "displayname":
                return arena.getDisplayName();
            case "group":
                return arena.getGroup();
            case "round":
                return String.valueOf(arena.getRound());
            case "time":
                return String.valueOf(arena.getTimer());
            case "time_formatted":
                return arena.getTimerFormatted();
            case "state":
                return arena.getGameState().getName();
            case "win_points":
                return String.valueOf(arena.getWinPoints());
            case "players":
                return String.valueOf(arena.getPlayers().size());
            case "max_players":
                return String.valueOf(arena.getMaxPlayers());
            case "kills":
                return String.valueOf(arena.getKills().getOrDefault(player, 0));
            case "deaths":
                return String.valueOf(arena.getDeaths().getOrDefault(player, 0));
            case "wool_placed":
                return String.valueOf(arena.getWoolPlaced().getOrDefault(player, 0));
            case "blocks_broken":
                return String.valueOf(arena.getBlocksBroken().getOrDefault(player, 0));
            case "round_kills":
                if (arena.getRoundTask() == null) return "0";
                return String.valueOf(arena.getRoundTask().getRoundKills().getOrDefault(player, 0));
            case "round_wool_placed":
                if (arena.getRoundTask() == null) return "0";
                return String.valueOf(arena.getRoundTask().getRoundPlacedWool().getOrDefault(player, 0));
            case "round_blocks_broken":
                if (arena.getRoundTask() == null) return "0";
                return String.valueOf(arena.getRoundTask().getRoundBrokenBlocks().getOrDefault(player, 0));
            case "team_letter":
                return team.getTeamLetter();
            case "team_color":
                return team.getTeamColor().getChatColor().toString();
            case "team_name":
                return team.getName();
        }
        return null;
    }

}
