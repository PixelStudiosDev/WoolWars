package me.cubecrafter.woolwars.utils;

import lombok.experimental.UtilityClass;
import com.cryptomorin.xseries.XMaterial;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.core.Arena;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
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

    public ChatColor getChatColor(String color) {
        return ChatColor.valueOf(color);
    }

    public ItemStack getWool(String color) {
        return XMaterial.matchXMaterial(color + "_WOOL").get().parseItem();
    }

    public String parsePlaceholders(String s, Arena arena) {
        return s.replace("{time}", arena.getTimerFormatted())
                .replace("{arenaname}", arena.getId())
                .replace("{arenadisplayname}", arena.getDisplayName());
    }

    public List<String> parsePlaceholders(List<String> lines, Arena arena) {
        List<String> parsed = new ArrayList<>();
        lines.forEach(s -> parsed.add(parsePlaceholders(s, arena)));
        return parsed;
    }

}
