package me.cubecrafter.woolwars.core;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.GameUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import me.cubecrafter.woolwars.utils.scoreboard.AssembleAdapter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class ScoreboardAdapter implements AssembleAdapter {

    private final YamlConfiguration messages = WoolWars.getInstance().getFileManager().getMessages();
    private final List<String> lobbyLines = TextUtil.color(messages.getStringList("scoreboard.lobby-board"));
    private final List<String> waitingLines =  TextUtil.color(messages.getStringList("scoreboard.waiting-board"));
    private final List<String> startingLines = TextUtil.color(messages.getStringList("scoreboard.starting-board"));
    private final List<String> playingLines = TextUtil.color(messages.getStringList("scoreboard.ingame-board"));
    private final String title = TextUtil.color(messages.getString("scoreboard.title"));

    @Override
    public String getTitle(Player player) {
        return title;
    }

    @Override
    public List<String> getLines(Player player) {
        if (GameUtil.isPlaying(player)) {
            Arena arena = GameUtil.getArenaByPlayer(player);
            switch (arena.getGameState()) {
                case WAITING:
                    return TextUtil.parsePlaceholders(waitingLines, arena);
                case STARTING:
                    return TextUtil.parsePlaceholders(startingLines, arena);
                case PLAYING:
                    return TextUtil.parsePlaceholders(playingLines, arena);
            }
        } else {
            return lobbyLines;
        }
        return null;
    }

}
