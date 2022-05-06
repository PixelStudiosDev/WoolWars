package me.cubecrafter.woolwars.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.database.PlayerData;
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
        if (player == null || params == null) return null;
        PlayerData data = WoolWars.getInstance().getPlayerDataManager().getPlayerData(player);
        switch (params) {
            case "wins":
                return String.valueOf(data.getWins());
            case "losses":
                return String.valueOf(data.getLosses());
            case "games_played":
                return String.valueOf(data.getGamesPlayed());
            case "kills":
                return String.valueOf(data.getKills());
            case "deaths":
                return String.valueOf(data.getDeaths());
            case "placed_blocks":
                return String.valueOf(data.getPlacedBlocks());
            case "broken_blocks":
                return String.valueOf(data.getBrokenBlocks());
            case "selected_kit":
                return data.getSelectedKit();
        }
        return null;
    }

}
