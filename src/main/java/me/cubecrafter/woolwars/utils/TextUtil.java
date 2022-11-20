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

package me.cubecrafter.woolwars.utils;

import com.cryptomorin.xseries.XPotion;
import com.cryptomorin.xseries.messages.ActionBar;
import com.cryptomorin.xseries.messages.Titles;
import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.storage.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class TextUtil {

    public String color(String s) {
        if (s == null) return "";
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public List<String> color(List<String> lines) {
        if (lines == null) return Collections.emptyList();
        lines.replaceAll(TextUtil::color);
        return lines;
    }

    public void info(String msg) {
        WoolWars.getInstance().getLogger().info(msg);
    }

    public String serializeLocation(Location location) {
        StringBuilder builder = new StringBuilder();
        builder.append(location.getWorld().getName()).append(",").append(location.getX()).append(",").append(location.getY()).append(",").append(location.getZ());
        if (location.getPitch() != 0 || location.getYaw() != 0) {
            builder.append(",").append(location.getYaw()).append(",").append(location.getPitch());
        }
        return builder.toString();
    }

    public Location deserializeLocation(String location) {
        String[] loc = location.split(",");
        return loc.length == 6 ? new Location(Bukkit.getWorld(loc[0]), Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Double.parseDouble(loc[3]), Float.parseFloat(loc[4]), Float.parseFloat(loc[5]))
                : new Location(Bukkit.getWorld(loc[0]), Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Double.parseDouble(loc[3]));
    }

    public String parsePlaceholders(Player player, String text) {
        if (WoolWars.getInstance().isPAPIEnabled()) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }
        return text;
    }

    public String format(CommandSender sender, String text) {
        Player player = sender instanceof Player ? (Player) sender : null;
        text = parsePlaceholders(player, text);
        text = text.replace("{date}", getCurrentDate()).replace("{prefix}", Messages.PREFIX.getAsString());
        for (String group : ArenaUtil.getGroups()) {
            text = text.replace("{count_" + group + "}", String.valueOf(ArenaUtil.getArenasByGroup(group).stream().mapToInt(arena -> arena.getPlayers().size()).sum()))
                    .replace("{count_total}", String.valueOf(ArenaUtil.getArenas().stream().mapToInt(arena -> arena.getPlayers().size()).sum()));
        }
        if (player != null) {
            PlayerData data = ArenaUtil.getPlayerData(player);
            if (data != null) {
                text = text.replace("{wins}", String.valueOf(data.getWins()))
                        .replace("{losses}", String.valueOf(data.getLosses()))
                        .replace("{games_played}", String.valueOf(data.getGamesPlayed()))
                        .replace("{kills}", String.valueOf(data.getKills()))
                        .replace("{deaths}", String.valueOf(data.getDeaths()))
                        .replace("{wool_placed}", String.valueOf(data.getWoolPlaced()))
                        .replace("{blocks_broken}", String.valueOf(data.getBlocksBroken()))
                        .replace("{powerups_collected}", String.valueOf(data.getPowerUpsCollected()))
                        .replace("{win_streak}", String.valueOf(data.getWinStreak()))
                        .replace("{highest_win_streak}", String.valueOf(data.getHighestWinStreak()));
            }
        }
        return color(text);
    }

    public String format(Player player, String text, Arena arena) {
        text = format(player, text);
        text = text.replace("{time_formatted}", arena.getTimerFormatted())
                .replace("{time}", String.valueOf(arena.getTimer()))
                .replace("{id}", arena.getId())
                .replace("{displayname}", arena.getDisplayName())
                .replace("{round}", String.valueOf(arena.getRound()))
                .replace("{group}", arena.getGroup())
                .replace("{state}", arena.getGameState().getName())
                .replace("{win_points}", String.valueOf(arena.getWinPoints()))
                .replace("{players}", String.valueOf(arena.getPlayers().size()))
                .replace("{max_players}", String.valueOf(arena.getMaxPlayers()));
        return text;
    }

    public List<String> format(CommandSender sender, List<String> lines) {
        if (lines == null) return Collections.emptyList();
        lines.replaceAll(line -> format(sender, line));
        return lines;
    }

    public List<String> format(Player player, List<String> lines, Arena arena) {
        if (lines == null) return Collections.emptyList();
        lines.replaceAll(line -> format(player, line, arena));
        return lines;
    }

    public void sendMessage(CommandSender sender, String message) {
        message = format(sender, message);
        if (message.startsWith("<center>") && message.endsWith("</center>")) {
            message = getCenteredMessage(message);
        }
        sender.sendMessage(message);
    }

    public void sendMessage(CommandSender sender, List<String> messages) {
        messages.forEach(message -> sendMessage(sender, message));
    }

    public void sendMessage(List<Player> players, String message) {
        players.forEach(player -> sendMessage(player, message));
    }

    public void sendMessage(List<Player> players, List<String> messages) {
        players.forEach(player -> sendMessage(player, messages));
    }

    public void sendTitle(Player player, int seconds, String title, String subtitle) {
        Titles.sendTitle(player, 0, seconds * 20, 0, format(player, title), format(player, subtitle));
    }

    public void sendTitle(List<Player> players, int seconds, String title, String subtitle) {
        players.forEach(player -> sendTitle(player, seconds, title, subtitle));
    }

    public void sendActionBar(Player player, String message) {
        ActionBar.sendActionBar(player, format(player, message));
    }

    public void sendActionBar(List<Player> players, String message) {
        players.forEach(player -> sendActionBar(player, message));
    }

    public String getCurrentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        return formatter.format(LocalDateTime.now());
    }

    public PotionEffect getEffect(String serialized) {
        String[] effect = serialized.split(",");
        PotionEffectType type = XPotion.matchXPotion(effect[0]).get().getPotionEffectType();
        int duration = Integer.parseInt(effect[1]) * 20;
        int amplifier = Integer.parseInt(effect[2]);
        return new PotionEffect(type, duration, amplifier);
    }

    public String getCenteredMessage(String message) {
        if (message == null || message.equals("")) return "";
        message = color(message.replace("<center>", "").replace("</center>", ""));
        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;
        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }
        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = 154 - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb + message;
    }

    public boolean matchMaterial(String material, String other) {
        boolean anyPrefix = material.startsWith("*");
        boolean anySuffix = material.endsWith("*");
        if (anyPrefix && anySuffix) {
            return other.contains(material.substring(1, material.length() - 1));
        } else if (anyPrefix) {
            return other.endsWith(material.substring(1));
        } else if (anySuffix) {
            return other.startsWith(material.substring(0, material.length() - 1));
        } else {
            return other.equals(material);
        }
    }

}
