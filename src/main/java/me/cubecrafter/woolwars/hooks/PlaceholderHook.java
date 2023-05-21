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

package me.cubecrafter.woolwars.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.ArenaUtil;
import me.cubecrafter.woolwars.storage.player.PlayerData;
import me.cubecrafter.woolwars.storage.player.PlayerManager;
import me.cubecrafter.woolwars.storage.player.StatisticType;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class PlaceholderHook extends PlaceholderExpansion {

    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    @Override
    public String getIdentifier() {
        return "woolwars";
    }

    @Override
    public String getAuthor() {
        return WoolWars.get().getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return WoolWars.get().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
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
        WoolPlayer woolPlayer = PlayerManager.get(player);
        PlayerData data = woolPlayer.getData();

        StatisticType type = StatisticType.fromId(params);
        if (type != null) {
            return String.valueOf(data.getStatistic(type));
        }

        switch (params) {
            case "selected_kit":
                return data.getSelectedKit();
            case "kdr":
                int deaths = data.getStatistic(StatisticType.DEATHS);
                int kills = data.getStatistic(StatisticType.KILLS);
                if (deaths == 0) {
                    return decimalFormat.format(kills);
                }
                return decimalFormat.format(kills / deaths);
        }

        if (!params.startsWith("arena_")) return null;
        Arena arena = ArenaUtil.getArenaByPlayer(woolPlayer);
        if (arena == null) return null;

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
                return arena.getState().getName();
            case "win_points":
                return String.valueOf(arena.getWinPoints());
            case "players":
                return String.valueOf(arena.getPlayers().size());
            case "max_players":
                return String.valueOf(arena.getMaxPlayers());
            case "kills":
                return String.valueOf(data.getArenaStatistic(StatisticType.KILLS));
            case "deaths":
                return String.valueOf(data.getArenaStatistic(StatisticType.DEATHS));
            case "wool_placed":
                return String.valueOf(data.getArenaStatistic(StatisticType.WOOL_PLACED));
            case "blocks_broken":
                return String.valueOf(data.getArenaStatistic(StatisticType.BLOCKS_BROKEN));
            case "round_kills":
                return String.valueOf(data.getRoundStatistic(StatisticType.KILLS));
            case "round_wool_placed":
                return String.valueOf(data.getRoundStatistic(StatisticType.WOOL_PLACED));
            case "round_blocks_broken":
                return String.valueOf(data.getRoundStatistic(StatisticType.BLOCKS_BROKEN));
        }
        return null;
    }

}
