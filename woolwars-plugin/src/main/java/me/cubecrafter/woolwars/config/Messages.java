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

    public String get() {
        return messages.getString(path);
    }

    public void send(Player player) {
        TextUtil.sendMessage(player, get());
    }

    public void send(List<Player> players) {
        TextUtil.sendMessage(players, get());
    }

}
