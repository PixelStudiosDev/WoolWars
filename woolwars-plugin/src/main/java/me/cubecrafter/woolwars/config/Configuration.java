package me.cubecrafter.woolwars.config;

import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

@RequiredArgsConstructor
public enum Configuration {

    LICENSE_KEY("license-key"),
    DISABLE_INTERACTION_BLOCKS("disable-interaction-blocks"),
    PLACEABLE_BLOCKS("placeable-blocks"),
    BLOCKED_COMMANDS("blocked-commands"),
    LOBBY_LOCATION("lobby-location");

    private final String path;
    private final YamlConfiguration config = WoolWars.getInstance().getFileManager().getConfig();

    public String getAsString() {
        return config.getString(path);
    }

    public int getAsInt() {
        return config.getInt(path);
    }

    public List<String> getAsStringList() {
        return config.getStringList(path);
    }

    public List<Integer> getAsIntegerList() {
        return config.getIntegerList(path);
    }

    public boolean getAsBoolean() {
        return config.getBoolean(path);
    }

    public Location getAsLocation() {
        return TextUtil.deserializeLocation(getAsString());
    }

}
