package me.cubecrafter.woolwars.utils;

import com.cryptomorin.xseries.XPotion;
import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.database.PlayerData;
import me.cubecrafter.woolwars.database.StatisticType;
import me.cubecrafter.woolwars.game.arena.Arena;
import me.cubecrafter.woolwars.game.team.Team;
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

    public String format(String s, Arena arena, Player player) {
        PlayerData data = WoolWars.getInstance().getPlayerDataManager().getPlayerData(player);
        s = s.replace("{wins}", String.valueOf(data.getStatistic(StatisticType.WINS)));
        String parsed = format(s)
                        .replace("{time}", arena.getTimerFormatted())
                        .replace("{id}", arena.getId())
                        .replace("{displayname}", arena.getDisplayName())
                        .replace("{round}", String.valueOf(arena.getRound()))
                        .replace("{group}", arena.getGroup())
                        .replace("{state}", arena.getGamePhase().getName())
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
            if (arena.getTeamByPlayer(player) != null && arena.getTeamByPlayer(player).equals(team)) {
                builder.append(TextUtil.color(" &7You"));
            }
            parsed = parsed.replace("{" + team.getName() + "_points_formatted}", builder.toString())
                    .replace("{" + team.getName() + "_points}", String.valueOf(team.getPoints()))
                    .replace("{" + team.getName() + "_players}", String.valueOf(team.getMembers().stream().filter(member ->  !arena.getDeadPlayers().contains(member)).count()));
        }
        return parsed;
    }

    public String format(String s) {
        if (WoolWars.getInstance().isPAPIEnabled()) {
            s = PlaceholderAPI.setPlaceholders(null, s);
        }
        String parsed = s.replace("{date}", TextUtil.getCurrentDate());
        for (String group : ArenaUtil.getGroups()) {
            parsed = parsed.replace("{" + group + "_players}", String.valueOf(ArenaUtil.getArenasByGroup(group).stream().mapToInt(arena -> arena.getPlayers().size()).sum()))
                    .replace("{total_players}", String.valueOf(ArenaUtil.getArenas().stream().mapToInt(arena -> arena.getPlayers().size()).sum()));
        }
        return color(parsed);
    }

    public String format(String s, Player player) {
        if (WoolWars.getInstance().isPAPIEnabled()) {
            s = PlaceholderAPI.setPlaceholders(player, s);
        }
        PlayerData data = ArenaUtil.getPlayerData(player);
        s = s.replace("{wins}", String.valueOf(data.getStatistic(StatisticType.WINS)))
                .replace("{losses}", String.valueOf(data.getStatistic(StatisticType.LOSSES)))
                .replace("{games_played}", String.valueOf(data.getStatistic(StatisticType.GAMES_PLAYED)))
                .replace("{kills}", String.valueOf(data.getStatistic(StatisticType.KILLS)))
                .replace("{deaths}", String.valueOf(data.getStatistic(StatisticType.DEATHS)))
                .replace("{placed_wool}", String.valueOf(data.getStatistic(StatisticType.PLACED_WOOL)))
                .replace("{broken_blocks}", String.valueOf(data.getStatistic(StatisticType.BROKEN_BLOCKS)))
                .replace("{powerups_collected}", String.valueOf(data.getStatistic(StatisticType.POWERUPS_COLLECTED)));
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

    public List<String> format(List<String> lines, Arena arena, Player player) {
        if (lines == null) return Collections.emptyList();
        List<String> parsed = new ArrayList<>();
        lines.forEach(s -> parsed.add(format(s, arena, player)));
        return parsed;
    }

    public String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        return dateFormat.format(new Date());
    }

    public PotionEffect getEffect(String serialized) {
        String[] effect = serialized.split(",");
        PotionEffectType type = XPotion.matchXPotion(effect[0]).get().getPotionEffectType();
        int duration = Integer.parseInt(effect[1]);
        int amplifier = Integer.parseInt(effect[2]);
        return new PotionEffect(type, duration, amplifier, false, false);
    }

}
