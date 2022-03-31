package me.cubecrafter.woolwars.core;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.GameUtil;
import me.cubecrafter.woolwars.utils.scoreboard.AssembleAdapter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Infinity
 * 31-03-2022 / 07:06 PM
 * WoolWars1 / me.cubecrafter.woolwars.core
 */

public class ScoreboardAdapter implements AssembleAdapter {

    private final List<String> lobbyLines = WoolWars.getInstance().getFileManager().getScoreboard().getStringList("scoreboard.lobby-board");
    private final List<String> waitingLines = WoolWars.getInstance().getFileManager().getScoreboard().getStringList("scoreboard.waiting-board");
    private final List<String> startingLines = WoolWars.getInstance().getFileManager().getScoreboard().getStringList("scoreboard.starting-board");
    private final List<String> ingameLines = WoolWars.getInstance().getFileManager().getScoreboard().getStringList("scoreboard.ingame-board");

    @Override
    public String getTitle(Player player) {
        return ChatColor.translateAlternateColorCodes('&', WoolWars.getInstance().getFileManager().getScoreboard().getString("scoreboard.title"));
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
                    return ingameLines;
            }
        } else {
            return lobbyLines;
        }

        return null;
    }
}
