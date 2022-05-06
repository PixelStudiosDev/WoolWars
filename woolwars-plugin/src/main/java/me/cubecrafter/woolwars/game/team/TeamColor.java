package me.cubecrafter.woolwars.game.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Color;

@Getter
@RequiredArgsConstructor
public enum TeamColor {

    RED(ChatColor.RED, Color.RED, "RED_WOOL", "RED_STAINED_GLASS"),
    BLUE(ChatColor.BLUE, Color.BLUE, "BLUE_WOOL", "BLUE_STAINED_GLASS"),
    GREEN(ChatColor.GREEN, Color.LIME, "LIME_WOOL", "LIME_STAINED_GLASS"),
    YELLOW(ChatColor.YELLOW, Color.YELLOW, "YELLOW_WOOL", "YELLOW_STAINED_GLASS"),
    AQUA(ChatColor.AQUA, Color.AQUA, "LIGHT_BLUE_WOOL", "LIGHT_BLUE_STAINED_GLASS"),
    WHITE(ChatColor.WHITE, Color.WHITE, "WHITE_WOOL", "WHITE_STAINED_GLASS"),
    PINK(ChatColor.LIGHT_PURPLE, Color.FUCHSIA, "PINK_WOOL", "PINK_STAINED_GLASS"),
    GRAY(ChatColor.GRAY, Color.GRAY, "GRAY_WOOL", "GRAY_STAINED_GLASS"),
    DARK_GREEN(ChatColor.DARK_GREEN, Color.GREEN, "GREEN_WOOL", "GREEN_STAINED_GLASS"),
    DARK_GRAY(ChatColor.DARK_GRAY, Color.fromRGB(74,74,74), "DARK_GRAY_WOOL", "DARK_GRAY_STAINED_GLASS");

    private final ChatColor chatColor;
    private final Color color;
    private final String woolMaterial;
    private final String glassMaterial;

}
