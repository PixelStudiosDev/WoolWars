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

package me.cubecrafter.woolwars.arena;

import lombok.experimental.UtilityClass;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.storage.player.StatisticType;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.xutils.TextUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ArenaUtil {

    private static final WoolWars plugin = WoolWars.get();

    public static void teleportToLobby(Player player) {
        if (Config.LOBBY_LOCATION.asString().equals("")) {
            TextUtil.sendMessage(player, "&cThe lobby location hasn't been set yet! Set it with /woolwars setlobby or contact an administrator.");
            return;
        }
        player.teleport(Config.LOBBY_LOCATION.asLocation());
    }

    public static boolean isBlockInTeamBase(Block block, Arena arena) {
        return arena.getTeams().stream().anyMatch(team -> team.getBase().isInside(block.getLocation()));
    }

    public static boolean isPlaying(WoolPlayer player) {
        return getArenas().stream().anyMatch(arena -> arena.isPlaying(player));
    }

    public static Arena getArenaByPlayer(WoolPlayer player) {
        return getArenas().stream().filter(arena -> arena.isPlaying(player)).findAny().orElse(null);
    }

    public static Arena getArenaById(String name) {
        return plugin.getArenaManager().getArena(name);
    }

    public static List<Arena> getArenasByGroup(String group) {
        if (group == null) {
            return getArenas();
        }
        return getArenas().stream().filter(arena -> arena.getGroup().equals(group)).collect(Collectors.toList());
    }

    public static List<String> getGroups() {
        return getArenas().stream().map(Arena::getGroup).distinct().collect(Collectors.toList());
    }

    public static List<Arena> getArenas() {
        return new ArrayList<>(plugin.getArenaManager().getArenas());
    }

    public static boolean joinRandomArena(WoolPlayer player, String group) {
        List<Arena> available = getArenasByGroup(group).stream().filter(Arena::isJoinable).collect(Collectors.toList());
        if (available.isEmpty()) {
            player.send(Messages.NO_ARENAS_AVAILABLE.asString());
            return false;
        }
        Arena random = available.stream().max(Comparator.comparing(arena -> arena.getPlayers().size())).orElse(available.get(0));
        random.addPlayer(player, true);
        return true;
    }

    public static boolean joinRandomArena(WoolPlayer player) {
        return joinRandomArena(player, null);
    }

    public static String parsePlaceholders(WoolPlayer player, String text) {
        text = TextUtil.parsePlaceholders(player.getPlayer(), text);
        text = text.replace("{date}", TextUtil.getCurrentDate("dd/MM/yyyy"));

        for (StatisticType type : StatisticType.values()) {
            text = text.replace("{" + type.getId() + "}", String.valueOf(player.getData().getStatistic(type)));
        }
        return text;
    }

    public static String parsePlaceholders(WoolPlayer player, String text, Arena arena) {
        text = parsePlaceholders(player, text);
        if (arena != null) {
            text = text.replace("{time_formatted}", arena.getTimerFormatted())
                    .replace("{time}", String.valueOf(arena.getTimer()))
                    .replace("{id}", arena.getId())
                    .replace("{displayname}", arena.getDisplayName())
                    .replace("{round}", String.valueOf(arena.getRound()))
                    .replace("{group}", arena.getGroup())
                    .replace("{state}", arena.getState().getName())
                    .replace("{win_points}", String.valueOf(arena.getWinPoints()))
                    .replace("{players}", String.valueOf(arena.getPlayers().size()))
                    .replace("{max_players}", String.valueOf(arena.getMaxPlayers()));
        }
        return text;
    }

    public static List<String> parsePlaceholders(WoolPlayer player, List<String> text) {
        text.replaceAll(line -> parsePlaceholders(player, line));
        return text;
    }

    public static List<String> parsePlaceholders(WoolPlayer player, List<String> text, Arena arena) {
        text.replaceAll(line -> parsePlaceholders(player, line, arena));
        return text;
    }

}
