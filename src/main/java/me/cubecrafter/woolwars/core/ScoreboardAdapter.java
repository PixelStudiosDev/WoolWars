package me.cubecrafter.woolwars.core;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.GameUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import me.cubecrafter.woolwars.utils.scoreboard.AssembleAdapter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class ScoreboardAdapter implements AssembleAdapter {

    private final List<String> lobbyLines = WoolWars.getInstance().getFileManager().getMessages().getStringList("scoreboard.lobby-board");
    private final List<String> waitingLines = WoolWars.getInstance().getFileManager().getMessages().getStringList("scoreboard.waiting-board");
    private final List<String> startingLines = WoolWars.getInstance().getFileManager().getMessages().getStringList("scoreboard.starting-board");
    private final List<String> playingLines = WoolWars.getInstance().getFileManager().getMessages().getStringList("scoreboard.ingame-board");

    @Override
    public String getTitle(Player player) {
        return TextUtil.color(WoolWars.getInstance().getFileManager().getMessages().getString("scoreboard.title"));
    }

    @Override
    public List<String> getLines(Player player) {
        if (GameUtil.isPlaying(player)) {
            Arena arena = GameUtil.getArenaByPlayer(player);
            switch (arena.getGameState()) {
                case WAITING:
                    return waitingLines;
                case STARTING:
                    return startingLines;
                case PLAYING:
                    return playingLines;
            }
        } else {
            return lobbyLines;
        }
        return null;
    }

}
