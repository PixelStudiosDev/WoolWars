package me.cubecrafter.woolwars.config;

import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public enum ConfigPath {

    PREFIX("prefix", WoolWars.getInstance().getFileManager().getMessages()),
    DISABLE_INTERACTION_BLOCKS("disable-interaction-blocks", WoolWars.getInstance().getFileManager().getConfig()),
    PLACEABLE_BLOCKS("placeable-blocks", WoolWars.getInstance().getFileManager().getConfig()),
    BLOCKED_COMMANDS("blocked-commands", WoolWars.getInstance().getFileManager().getConfig()),
    LOBBY_LOCATION("lobby-location", WoolWars.getInstance().getFileManager().getConfig());

    private final String path;
    private final YamlConfiguration file;

    public String getAsString() {
        return file.getString(path);
    }

    public int getAsInt() {
        return file.getInt(path);
    }

    public List<String> getAsStringList() {
        return file.getStringList(path);
    }

    public List<Integer> getAsIntegerList() {
        return file.getIntegerList(path);
    }

    public boolean getAsBoolean() {
        return file.getBoolean(path);
    }

    public Location getAsLocation() {
        return TextUtil.deserializeLocation(getAsString());
    }

    public boolean isSet() {
        return file.isSet(path);
    }

    public Set<String> getKeys() {
        return file.getConfigurationSection(path).getKeys(false);
    }

}
