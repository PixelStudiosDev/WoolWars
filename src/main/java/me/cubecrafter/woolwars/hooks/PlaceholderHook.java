package me.cubecrafter.woolwars.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.cubecrafter.woolwars.WoolWars;
import org.bukkit.entity.Player;

public class PlaceholderHook extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "woolwars";
    }

    @Override
    public String getAuthor() {
        return "CubeCrafter";
    }

    @Override
    public String getVersion() {
        return WoolWars.getInstance().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        return null;
    }

}
