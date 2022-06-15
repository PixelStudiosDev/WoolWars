package me.cubecrafter.woolwars.config;

import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

@RequiredArgsConstructor
public enum Messages {

    PREFIX("prefix");

    private final String path;
    private final YamlConfiguration messages = WoolWars.getInstance().getFileManager().getMessages();

    public String getAsString() {
        return messages.getString(path);
    }

    public List<String> getAsStringList() {
        return messages.getStringList(path);
    }

    public void send(Player player) {
        if (messages.isString(path)) {
            TextUtil.sendMessage(player, messages.getString(path));
        } else if (messages.isList(path)) {
            List<String> list = messages.getStringList(path);
            list.forEach(s -> TextUtil.sendMessage(player, s));
        }
    }

    public void send(List<Player> players) {
        players.forEach(this::send);
    }

}
