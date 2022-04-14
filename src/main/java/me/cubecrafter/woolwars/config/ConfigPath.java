package me.cubecrafter.woolwars.config;

import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.WoolWars;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public enum ConfigPath {

    PREFIX("prefix", WoolWars.getInstance().getFileManager().getMessages()),
    DISABLED_INTERACTION_BLOCKS("disabled-interaction-blocks", WoolWars.getInstance().getFileManager().getConfig());

    private final String path;
    private final YamlConfiguration file;

    public String getString() {
        return file.getString(path);
    }

    public int getInt() {
        return file.getInt(path);
    }

    public List<String> getStringList() {
        return file.getStringList(path);
    }

    public List<Integer> getIntegerList() {
        return file.getIntegerList(path);
    }

    public boolean getBoolean() {
        return file.getBoolean(path);
    }

    public boolean isSet() {
        return file.isSet(path);
    }

    public Set<String> getKeys() {
        return file.getConfigurationSection(path).getKeys(false);
    }

}
