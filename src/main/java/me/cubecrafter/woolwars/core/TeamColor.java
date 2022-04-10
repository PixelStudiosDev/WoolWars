package me.cubecrafter.woolwars.core;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Color;

public enum TeamColor {

    RED(ChatColor.RED, Color.RED),
    BLUE(ChatColor.BLUE, Color.BLUE),
    GREEN(ChatColor.GREEN, Color.LIME),
    YELLOW(ChatColor.YELLOW, Color.YELLOW),
    AQUA(ChatColor.AQUA, Color.AQUA),
    WHITE(ChatColor.WHITE, Color.WHITE),
    PINK(ChatColor.LIGHT_PURPLE, Color.FUCHSIA),
    GRAY(ChatColor.GRAY, Color.GRAY),
    DARK_GREEN(ChatColor.DARK_GREEN, Color.GREEN),
    DARK_GRAY(ChatColor.DARK_GRAY, Color.fromRGB(74,74,74));

    @Getter private final ChatColor chatColor;
    @Getter private final Color color;

    TeamColor(ChatColor chatColor, Color color) {
        this.chatColor = chatColor;
        this.color = color;
    }

}
