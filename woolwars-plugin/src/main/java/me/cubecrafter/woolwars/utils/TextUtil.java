package me.cubecrafter.woolwars.utils;

import com.cryptomorin.xseries.XPotion;
import com.cryptomorin.xseries.messages.ActionBar;
import com.cryptomorin.xseries.messages.Titles;
import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.api.arena.Arena;
import me.cubecrafter.woolwars.api.arena.GameState;
import me.cubecrafter.woolwars.api.database.PlayerData;
import me.cubecrafter.woolwars.api.team.TeamColor;
import me.cubecrafter.woolwars.config.Messages;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

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

    public TextComponent buildTextComponent(String msg, String hover, String click, ClickEvent.Action clickAction){
        TextComponent component = new TextComponent(color(msg));
        if (hover != null) {
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(hover)).create()));
        }
        component.setClickEvent(new ClickEvent(clickAction, click));
        return component;
    }

    public void info(String msg) {
        WoolWars.getInstance().getLogger().info(msg);
    }

    public void severe(String msg) {
        WoolWars.getInstance().getLogger().severe(msg);
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

    public String format(String s, Arena arena, Player player) {
        s = format(s, player)
                .replace("{time_formatted}", arena.getTimerFormatted())
                .replace("{time}", String.valueOf(arena.getTimer()))
                .replace("{id}", arena.getId())
                .replace("{displayname}", arena.getDisplayName())
                .replace("{round}", String.valueOf(arena.getRound()))
                .replace("{group}", arena.getGroup())
                .replace("{state}", getStateName(arena.getGameState()))
                .replace("{win_points}", String.valueOf(arena.getWinPoints()))
                .replace("{players}", String.valueOf(arena.getPlayers().size()))
                .replace("{max_players}", String.valueOf(arena.getMaxPlayersPerTeam() * arena.getTeams().size()));
        return s;
    }

    public String format(String s) {
        if (WoolWars.getInstance().isPAPIEnabled()) {
            s = PlaceholderAPI.setPlaceholders(null, s);
        }
        s = s.replace("{date}", TextUtil.getCurrentDate());
        for (String group : ArenaUtil.getGroups()) {
            s = s.replace("{count_" + group + "}", String.valueOf(ArenaUtil.getArenasByGroup(group).stream().mapToInt(arena -> arena.getPlayers().size()).sum()))
                    .replace("{count_total}", String.valueOf(ArenaUtil.getArenas().stream().mapToInt(arena -> arena.getPlayers().size()).sum()));
        }
        return color(s);
    }

    public String format(String s, Player player) {
        if (WoolWars.getInstance().isPAPIEnabled()) {
            s = PlaceholderAPI.setPlaceholders(player, s);
        }
        PlayerData data = ArenaUtil.getPlayerData(player);
        if (data != null) {
            s = s.replace("{wins}", String.valueOf(data.getWins()))
                    .replace("{losses}", String.valueOf(data.getLosses()))
                    .replace("{games_played}", String.valueOf(data.getGamesPlayed()))
                    .replace("{kills}", String.valueOf(data.getKills()))
                    .replace("{deaths}", String.valueOf(data.getDeaths()))
                    .replace("{wool_placed}", String.valueOf(data.getWoolPlaced()))
                    .replace("{blocks_broken}", String.valueOf(data.getBlocksBroken()))
                    .replace("{powerups_collected}", String.valueOf(data.getPowerUpsCollected()));
        }
        return format(s);
    }

    public List<String> format(List<String> lines, Player player) {
        if (lines == null) return Collections.emptyList();
        lines.replaceAll(s -> format(s, player));
        return lines;
    }

    public List<String> format(List<String> lines) {
        if (lines == null) return Collections.emptyList();
        lines.replaceAll(TextUtil::format);
        return lines;
    }

    public List<String> format(List<String> lines, Arena arena, Player player) {
        if (lines == null) return Collections.emptyList();
        lines.replaceAll(s -> format(s, arena, player));
        return lines;
    }

    public void sendMessage(Player player, String message) {
        message = message.replace("{prefix}", Messages.PREFIX.getAsString());
        if (message.startsWith("<center>") && message.endsWith("</center>")) {
            message = getCenteredMessage(message);
        }
        player.sendMessage(format(message, player));
    }

    public void sendMessage(Player player, List<String> messages) {
        messages.forEach(message -> sendMessage(player, message));
    }

    public void sendMessage(List<Player> players, String message) {
        players.forEach(player -> sendMessage(player, message));
    }

    public void sendTitle(Player player, int seconds, String title, String subtitle) {
        Titles.sendTitle(player, 0, seconds * 20, 0, format(title, player), format(subtitle, player));
    }

    public void sendTitle(List<Player> players, int seconds, String title, String subtitle) {
        players.forEach(player -> sendTitle(player, seconds, title, subtitle));
    }

    public void sendActionBar(Player player, String message) {
        ActionBar.sendActionBar(player, format(message, player));
    }

    public void sendActionBar(List<Player> players, String message) {
        players.forEach(player -> sendActionBar(player, message));
    }

    public void sendActionBarWhile(Player player, String message, Callable<Boolean> condition) {
        ActionBar.sendActionBarWhile(WoolWars.getInstance(), player, format(message, player), condition);
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
        return new PotionEffect(type, duration, amplifier, false, false);
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

    public String getStateName(GameState state) {
        switch (state) {
            case WAITING:
                return Messages.GAME_STATE_WAITING.getAsString();
            case STARTING:
                return Messages.GAME_STATE_STARTING.getAsString();
            case PRE_ROUND:
                return Messages.GAME_STATE_PRE_ROUND.getAsString();
            case ACTIVE_ROUND:
                return Messages.GAME_STATE_ACTIVE_ROUND.getAsString();
            case ROUND_OVER:
                return Messages.GAME_STATE_ROUND_OVER.getAsString();
            case GAME_ENDED:
                return Messages.GAME_STATE_GAME_ENDED.getAsString();
        }
        return null;
    }

}
