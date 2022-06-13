package me.cubecrafter.woolwars.utils;

import com.cryptomorin.xseries.XPotion;
import com.cryptomorin.xseries.messages.ActionBar;
import com.cryptomorin.xseries.messages.Titles;
import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.GameArena;
import me.cubecrafter.woolwars.api.database.PlayerData;
import me.cubecrafter.woolwars.team.GameTeam;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@UtilityClass
public class TextUtil {

    public String color(String s) {
        if (s == null || s.isEmpty()) return "";
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public List<String> color(List<String> lines) {
        if (lines == null) return Collections.emptyList();
        List<String> color = new ArrayList<>();
        lines.forEach(s -> color.add(color(s)));
        return color;
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
            builder.append(",").append(location.getPitch()).append(",").append(location.getYaw());
        }
        return builder.toString();
    }

    public Location deserializeLocation(String location) {
        String[] loc = location.split(",");
        return loc.length == 6 ? new Location(Bukkit.getWorld(loc[0]), Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Double.parseDouble(loc[3]), Float.parseFloat(loc[4]), Float.parseFloat(loc[5]))
                : new Location(Bukkit.getWorld(loc[0]), Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Double.parseDouble(loc[3]));
    }

    public String format(String s, GameArena arena, Player player) {
        s = format(s)
                        .replace("{time}", arena.getTimerFormatted())
                        .replace("{id}", arena.getId())
                        .replace("{displayname}", arena.getDisplayName())
                        .replace("{round}", String.valueOf(arena.getRound()))
                        .replace("{group}", arena.getGroup())
                        .replace("{state}", arena.getGameState().getName())
                        .replace("{required_points}", String.valueOf(arena.getRequiredPoints()))
                        .replace("{players}", String.valueOf(arena.getPlayers().size()))
                        .replace("{max_players}", String.valueOf(arena.getMaxPlayersPerTeam() * arena.getTeams().size()));
        for (GameTeam team : arena.getTeams()) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < arena.getRequiredPoints(); i++) {
                if (team.getPoints() <= i) {
                    builder.append(TextUtil.color("&7⬤"));
                } else {
                    builder.append(TextUtil.color(team.getTeamColor().getChatColor() + "⬤"));
                }
            }
            s = s.replace("{" + team.getName() + "_points_formatted}", builder.toString())
                    .replace("{" + team.getName() + "_points}", String.valueOf(team.getPoints()))
                    .replace("{" + team.getName() + "_alive}", String.valueOf(team.getMembers().stream().filter(arena::isAlive).count()));
        }
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
        s = s.replace("{wins}", String.valueOf(data.getWins()))
                .replace("{losses}", String.valueOf(data.getLosses()))
                .replace("{games_played}", String.valueOf(data.getGamesPlayed()))
                .replace("{kills}", String.valueOf(data.getKills()))
                .replace("{deaths}", String.valueOf(data.getDeaths()))
                .replace("{placed_wool}", String.valueOf(data.getPlacedWool()))
                .replace("{broken_blocks}", String.valueOf(data.getBrokenBlocks()))
                .replace("{powerups_collected}", String.valueOf(data.getPowerUpsCollected()));
        return format(s);
    }

    public List<String> format(List<String> lines, Player player) {
        if (lines == null) return Collections.emptyList();
        List<String> parsed = new ArrayList<>();
        lines.forEach(s -> parsed.add(format(s, player)));
        return parsed;
    }

    public List<String> format(List<String> lines) {
        if (lines == null) return Collections.emptyList();
        List<String> parsed = new ArrayList<>();
        lines.forEach(s -> parsed.add(format(s)));
        return parsed;
    }

    public List<String> format(List<String> lines, GameArena arena, Player player) {
        if (lines == null) return Collections.emptyList();
        List<String> parsed = new ArrayList<>();
        lines.forEach(s -> parsed.add(format(s, arena, player)));
        return parsed;
    }

    public void sendMessage(Player player, String message) {
        player.sendMessage(format(message, player));
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

    public String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        return dateFormat.format(new Date());
    }

    public PotionEffect getEffect(String serialized) {
        String[] effect = serialized.split(",");
        PotionEffectType type = XPotion.matchXPotion(effect[0]).get().getPotionEffectType();
        int duration = Integer.parseInt(effect[1]) * 20;
        int amplifier = Integer.parseInt(effect[2]);
        return new PotionEffect(type, duration, amplifier, false, false);

    }

}
