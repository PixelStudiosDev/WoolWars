package me.cubecrafter.woolwars.utils;

import me.cubecrafter.woolwars.WoolWars;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class TextUtil {

    public static String color(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> color(List<String> lines){
        List<String> color = new ArrayList<>();
        lines.forEach(s -> color.add(color(s)));
        return color;
    }

    public static TextComponent buildTextComponent(String msg, String hover, String click, ClickEvent.Action clickAction){
        TextComponent component = new TextComponent(color(msg));
        if (hover != null) {
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color(hover)).create()));
        }
        component.setClickEvent(new ClickEvent(clickAction, click));
        return component;
    }

    public static void info(String msg) {
        WoolWars.getInstance().getLogger().info(msg);
    }

    public static void warn(String msg) {
        WoolWars.getInstance().getLogger().warning(msg);
    }

    public static void severe(String msg) {
        WoolWars.getInstance().getLogger().severe(msg);
    }

    public static String serializeLocation(Location location) {
        return location.getWorld().getName() + ":" +
                location.getX() + ":" +
                location.getY() + ":" +
                location.getZ() + ":" +
                location.getPitch() + ":" +
                location.getYaw();
    }

    public static Location deserializeLocation(String location) {
        String[] loc = location.split(":");
        return new Location(Bukkit.getWorld(loc[0]), Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Double.parseDouble(loc[3]), Float.parseFloat(loc[4]), Float.parseFloat(loc[5]));
    }

    public static ChatColor getChatColor(String color) {
        return ChatColor.valueOf(color);
    }

}
