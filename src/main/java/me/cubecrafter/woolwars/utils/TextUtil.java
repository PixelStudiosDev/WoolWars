package me.cubecrafter.woolwars.utils;

import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.Team;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@UtilityClass
public class TextUtil {

    public String color(String s){
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

    public void warn(String msg) {
        WoolWars.getInstance().getLogger().warning(msg);
    }

    public void severe(String msg) {
        WoolWars.getInstance().getLogger().severe(msg);
    }

    public String serializeLocation(Location location) {
        return location.getWorld().getName() + ":" +
                location.getX() + ":" +
                location.getY() + ":" +
                location.getZ() + ":" +
                location.getPitch() + ":" +
                location.getYaw();
    }

    public Location deserializeLocation(String location) {
        String[] loc = location.split(":");
        return new Location(Bukkit.getWorld(loc[0]), Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Double.parseDouble(loc[3]), Float.parseFloat(loc[4]), Float.parseFloat(loc[5]));
    }

    public String format(String s, Arena arena) {
        String parsed = format(s)
                        .replace("{time}", arena.getTimerFormatted())
                        .replace("{id}", arena.getId())
                        .replace("{displayname}", arena.getDisplayName())
                        .replace("{round}", String.valueOf(arena.getRound()))
                        .replace("{group}", arena.getGroup())
                        .replace("{state}", arena.getGameState().getName())
                        .replace("{required_points}", String.valueOf(arena.getRequiredPoints()))
                        .replace("{players}", String.valueOf(arena.getPlayers().size()))
                        .replace("{max_players}", String.valueOf(arena.getMaxPlayersPerTeam() * arena.getTeams().size()));
        for (Team team : arena.getTeams()) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < arena.getRequiredPoints(); i++) {
                if (team.getPoints() <= i) {
                    builder.append(TextUtil.color("&7⬤"));
                } else {
                    builder.append(TextUtil.color(team.getTeamColor().getChatColor() + "⬤"));
                }
            }
            parsed = parsed.replace("{" + team.getName() + "_points_formatted}", builder.toString())
                    .replace("{" + team.getName() + "_points}", String.valueOf(team.getPoints()))
                    .replace("{" + team.getName() + "_players}", String.valueOf(team.getMembers().stream().filter(player ->  !arena.getDeadPlayers().contains(player)).count()));
        }
        return parsed;
    }

    public String format(String s) {
        s = PlaceholderAPI.setPlaceholders(null, s);
        String parsed = s.replace("{date}", TextUtil.getCurrentDate());
        for (String group : GameUtil.getGroups()) {
            parsed = parsed.replace("{" + group + "_players}", String.valueOf(GameUtil.getArenasByGroup(group).stream().mapToInt(arena -> arena.getPlayers().size()).sum()))
                    .replace("{total_players}", String.valueOf(GameUtil.getArenas().stream().mapToInt(arena -> arena.getPlayers().size()).sum()));
        }
        return color(parsed);
    }

    public String format(String s, Player player) {
        s = PlaceholderAPI.setPlaceholders(player, s);
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

    public List<String> format(List<String> lines, Arena arena) {
        if (lines == null) return Collections.emptyList();
        List<String> parsed = new ArrayList<>();
        lines.forEach(s -> parsed.add(format(s, arena)));
        return parsed;
    }

    public String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        return dateFormat.format(new Date());
    }

}
