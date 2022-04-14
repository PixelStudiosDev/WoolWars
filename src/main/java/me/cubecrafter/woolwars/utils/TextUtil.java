package me.cubecrafter.woolwars.utils;

import lombok.experimental.UtilityClass;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.core.Arena;
import me.cubecrafter.woolwars.core.Team;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@UtilityClass
public class TextUtil {

    public String color(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public List<String> color(List<String> lines){
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

    public String parsePlaceholders(String s, Arena arena) {
        String parsed = s.replace("{time}", arena.getTimerFormatted())
                        .replace("{arena_name}", arena.getId())
                        .replace("{arena_displayname}", arena.getDisplayName())
                        .replace("{date}", TextUtil.getCurrentDate())
                        .replace("{round}", String.valueOf(arena.getRound()))
                        .replace("{arena_state}", arena.getGameState().getName())
                        .replace("{required_points}", String.valueOf(arena.getRequiredPoints()))
                        .replace("{total_players}", String.valueOf(arena.getPlayers().size()))
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

    public String parsePlaceholders(String s) {
        return s.replace("{date}", TextUtil.getCurrentDate());
    }

    public List<String> parsePlaceholders(List<String> lines) {
        List<String> parsed = new ArrayList<>();
        lines.forEach(s -> parsed.add(parsePlaceholders(s)));
        return parsed;
    }

    public List<String> parsePlaceholders(List<String> lines, Arena arena) {
        List<String> parsed = new ArrayList<>();
        lines.forEach(s -> parsed.add(parsePlaceholders(s, arena)));
        return parsed;
    }

    public String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        return dateFormat.format(new Date());
    }

}
