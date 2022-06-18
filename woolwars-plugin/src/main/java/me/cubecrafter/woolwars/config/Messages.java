package me.cubecrafter.woolwars.config;

import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.entity.Player;

import java.util.List;

@RequiredArgsConstructor
public enum Messages {

    PREFIX("prefix"),
    ARENA_NOT_FOUND("arena-not-found"),
    NO_ARENAS_AVAILABLE("no-arenas-available"),
    ALREADY_IN_ARENA("already-in-arena"),
    GAME_ALREADY_STARTED("game-already-started"),
    ARENA_FULL("arena-full"),
    PLAYER_JOIN_ARENA("player-join-arena"),
    PLAYER_LEAVE_ARENA("player-leave-arena"),
    START_CANCELLED("start-cancelled"),
    UNKNOWN_COMMAND("unknown-command"),
    ONLY_PLAYER_COMMAND("only-player-command"),
    NO_PERMISSION("no-permission"),
    CANT_BREAK_BLOCK("cant-break-block"),
    CANT_PLACE_BLOCK("cant-place-block"),
    COMMAND_BLOCKED("command-blocked"),
    DEATH_TITLE("death-title"),
    DEATH_SUBTITLE("death-subtitle"),
    ALL_PLAYERS_DEAD("all-players-dead"),
    DEATH_BY_FALL("death-by-fall"),
    DEATH_BY_LAVA("death-by-lava"),
    DEATH_BY_VOID("death-by-void"),
    DEATH_GENERIC("death-generic"),
    KILL_MESSAGE("kill-message"),
    KIT_ALREADY_SELECTED("kit-already-selected"),
    TIME_LEFT_COUNTDOWN("time-left-countdown"),
    TEAM_WON_FORMAT("team-won-format"),
    TEAM_LOST_FORMAT("team-lost-format"),
    NONE_FORMAT("none-format"),
    END_GAME_STATS_FORMAT("end-game-stats-format"),
    END_GAME_MESSAGE("end-game-message"),
    WINNER_TITLE("winner-title"),
    WINNER_SUBTITLE("winner-subtitle"),
    LOSER_TITLE("loser-title"),
    LOSER_SUBTITLE("loser-subtitle"),
    ROUND_WIN_TITLE("round-win-title"),
    ROUND_WIN_SUBTITLE("round-win-subtitle"),
    ROUND_LOSE_TITLE("round-lose-title"),
    ROUND_LOSE_SUBTITLE("round-lose-subtitle"),
    ROUND_DRAW_TITLE("round-draw-title"),
    ROUND_DRAW_SUBTITLE("round-draw-subtitle"),
    ROUND_START_COUNTDOWN_TITLE("round-start-countdown-title"),
    ROUND_START_COUNTDOWN_SUBTITLE("round-start-countdown-subtitle"),
    GAME_START_MESSAGE("game-start-message"),
    NO_STATS_ACHIEVED("no-stats-achieved"),
    STATS_MESSAGE("stats-message"),
    STATS_KILLS("stats-kills"),
    STATS_PLACED_WOOL("stats-placed-wool"),
    STATS_BROKEN_BLOCKS("stats-broken-blocks"),
    PRE_ROUND_TITLE("pre-round-title"),
    PRE_ROUND_SUBTITLE("pre-round-subtitle"),
    ROUND_START_TITLE("round-start-title"),
    ROUND_START_SUBTITLE("round-start-subtitle"),
    SHIFT_TO_SELECT_KIT("shift-to-select-kit"),
    GAME_START_COUNTDOWN("game-start-countdown"),
    SCOREBOARD_TEAM_FORMAT("scoreboard.team-format"),
    SCOREBOARD_TITLE("scoreboard.title"),
    SCOREBOARD_LOBBY("scoreboard.lobby"),
    SCOREBOARD_WAITING("scoreboard.waiting"),
    SCOREBOARD_STARTING("scoreboard.starting"),
    SCOREBOARD_PLAYING("scoreboard.playing");

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
