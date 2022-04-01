package me.cubecrafter.woolwars.core;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;

public enum TeamColor {

    RED,
    BLUE,
    GREEN,
    YELLOW,
    AQUA,
    WHITE,
    PINK,
    GRAY,
    DARK_GREEN,
    DARK_GRAY;

    public ChatColor getChatColor() {
        ChatColor color;
        TeamColor teamColor = TeamColor.valueOf(this.toString());
        if (teamColor == TeamColor.PINK) {
            color = ChatColor.LIGHT_PURPLE;
        } else {
            color = ChatColor.valueOf(teamColor.toString());
        }
        return color;
    }

    public Color getColor() {
        Color color = Color.WHITE;
        switch (this) {
            case PINK:
                color = Color.FUCHSIA;
                break;
            case GRAY:
                color = Color.GRAY;
                break;
            case BLUE:
                color = Color.BLUE;
                break;
            case WHITE:
                break;
            case DARK_GREEN:
                color = Color.GREEN;
                break;
            case AQUA:
                color = Color.AQUA;
                break;
            case RED:
                color = Color.RED;
                break;
            case GREEN:
                color = Color.LIME;
                break;
            case YELLOW:
                color = Color.YELLOW;
                break;
            case DARK_GRAY:
                color = Color.fromBGR(74, 74, 74);
                break;
        }
        return color;
    }

    public Material getWoolMaterial() {
        String color = "WHITE_WOOL";
        switch (this) {
            case PINK:
                color = "PINK_WOOL";
                break;
            case GRAY:
                color = "LIGHT_GRAY_WOOL";
                break;
            case DARK_GRAY:
                color = "GRAY_WOOL";
                break;
            case BLUE:
                color = "BLUE_WOOL";
                break;
            case WHITE:
                color = "WHITE_WOOL";
                break;
            case DARK_GREEN:
                color = "GREEN_WOOL";
                break;
            case AQUA:
                color = "LIGHT_BLUE_WOOL";
                break;
            case GREEN:
                color = "LIME_WOOL";
                break;
            case YELLOW:
                color = "YELLOW_WOOL";
                break;
            case RED:
                color = "RED_WOOL";
                break;
        }
        return XMaterial.matchXMaterial(color).get().parseMaterial();
    }

}
