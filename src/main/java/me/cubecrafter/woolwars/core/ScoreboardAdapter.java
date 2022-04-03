package me.cubecrafter.woolwars.core;

import com.sun.org.apache.regexp.internal.RE;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.GameUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import me.cubecrafter.woolwars.utils.scoreboard.view.ViewContext;
import me.cubecrafter.woolwars.utils.scoreboard.view.ViewProvider;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class ScoreboardAdapter implements ViewProvider {

    private final YamlConfiguration messages = WoolWars.getInstance().getFileManager().getMessages();
    private final List<String> lobbyLines = TextUtil.color(messages.getStringList("scoreboard.lobby-board"));
    private final List<String> waitingLines =  TextUtil.color(messages.getStringList("scoreboard.waiting-board"));
    private final List<String> startingLines = TextUtil.color(messages.getStringList("scoreboard.starting-board"));
    private final List<String> playingLines = TextUtil.color(messages.getStringList("scoreboard.ingame-board"));
    private final String title = TextUtil.color(messages.getString("scoreboard.title"));

    @Override
    public String getTitle(ViewContext context) {
        return title;
    }

    @Override
    public List<String> getLines(ViewContext context) {
        Player player = context.getPlayer();
        if (GameUtil.isPlaying(player)) {
            Arena arena = GameUtil.getArenaByPlayer(player);
            switch (arena.getGameState()) {
                case WAITING:
                    return TextUtil.parsePlaceholders(waitingLines, arena);
                case STARTING:
                    return TextUtil.parsePlaceholders(startingLines, arena);
                case PLAYING:
                case SELECTING_KIT:
                    return TextUtil.parsePlaceholders(playingLines, arena);
            }
        }
        return lobbyLines;
    }

}
