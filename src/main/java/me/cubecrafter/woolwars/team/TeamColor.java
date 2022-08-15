package me.cubecrafter.woolwars.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Color;

@Getter
@RequiredArgsConstructor
public enum TeamColor {

    RED(ChatColor.RED, Color.RED, "RED_WOOL"),
    BLUE(ChatColor.BLUE, Color.BLUE, "BLUE_WOOL"),
    GREEN(ChatColor.GREEN, Color.LIME, "LIME_WOOL"),
    YELLOW(ChatColor.YELLOW, Color.YELLOW, "YELLOW_WOOL"),
    AQUA(ChatColor.AQUA, Color.AQUA, "LIGHT_BLUE_WOOL"),
    WHITE(ChatColor.WHITE, Color.WHITE, "WHITE_WOOL"),
    PINK(ChatColor.LIGHT_PURPLE, Color.FUCHSIA, "PINK_WOOL"),
    GRAY(ChatColor.GRAY, Color.GRAY, "LIGHT_GRAY_WOOL"),
    DARK_GREEN(ChatColor.DARK_GREEN, Color.GREEN, "GREEN_WOOL"),
    PURPLE(ChatColor.DARK_PURPLE, Color.PURPLE, "PURPLE_WOOL"),
    ORANGE(ChatColor.GOLD, Color.ORANGE, "ORANGE_WOOL"),
    DARK_GRAY(ChatColor.DARK_GRAY, Color.fromRGB(75,75,75), "GRAY_WOOL"),
    DARK_AQUA(ChatColor.DARK_AQUA, Color.fromRGB(0, 150, 150), "CYAN_WOOL");

    private final ChatColor chatColor;
    private final Color color;
    private final String woolMaterial;

}
