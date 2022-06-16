package me.cubecrafter.woolwars.config;

import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.entity.Player;

import java.util.List;

@RequiredArgsConstructor
public enum Messages {

    PREFIX("prefix");

    private final String path;

    public String getAsString() {
        return WoolWars.getInstance().getFileManager().getMessages().getString(path);
    }

    public List<String> getAsStringList() {
        return WoolWars.getInstance().getFileManager().getMessages().getStringList(path);
    }

    public void send(Player player) {
        if (WoolWars.getInstance().getFileManager().getMessages().isString(path)) {
            TextUtil.sendMessage(player, WoolWars.getInstance().getFileManager().getMessages().getString(path));
        } else if (WoolWars.getInstance().getFileManager().getMessages().isList(path)) {
            List<String> list = WoolWars.getInstance().getFileManager().getMessages().getStringList(path);
            list.forEach(s -> TextUtil.sendMessage(player, s));
        }
    }

    public void send(List<Player> players) {
        players.forEach(this::send);
    }

}
