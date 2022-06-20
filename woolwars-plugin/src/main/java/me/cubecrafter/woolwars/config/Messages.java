package me.cubecrafter.woolwars.config;

import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.entity.Player;

import java.util.List;

@RequiredArgsConstructor
public enum Messages {

    PREFIX("prefix"),
    ARENA_NOT_FOUND("general.arena-not-found"),
    NO_ARENAS_AVAILABLE("general.no-arenas-available"),
    ALREADY_IN_ARENA("general.already-in-arena"),
    GAME_ALREADY_STARTED("general.game-already-started"),
    ARENA_FULL("general.arena-full"),
    PLAYER_JOIN_ARENA("game.player-join"),
    PLAYER_LEAVE_ARENA("game.player-leave"),
    START_CANCELLED("game.start-cancelled"),
    UNKNOWN_COMMAND("general.unknown-command"),
    ONLY_PLAYER_COMMAND("general.only-player-command"),
    NO_PERMISSION("general.no-permission"),
    CANT_BREAK_BLOCK("game.cant-break-block"),
    CANT_PLACE_BLOCK("game.cant-place-block"),
    COMMAND_BLOCKED("general.command-blocked"),
    DEATH_TITLE("game.player-death.title"),
    DEATH_SUBTITLE("game.player-death.subtitle"),
    ALL_PLAYERS_DEAD("game.all-players-dead"),
    DEATH_BY_FALL("game.player-death.fall"),
    DEATH_BY_LAVA("game.player-death.lava"),
    DEATH_BY_VOID("game.player-death.void"),
    DEATH_GENERIC("game.player-death.generic"),
    KILL_MESSAGE("game.player-death.kill-message"),
    KIT_ALREADY_SELECTED("game.pre-round.kit-already-selected"),
    KIT_SELECTED("game.pre-round.kit-selected"),
    TIME_LEFT_COUNTDOWN("game.round-time-left-countdown"),
    TEAM_WON_FORMAT("game.game-end.stats-message.team-winner-format"),
    TEAM_LOST_FORMAT("game.game-end.stats-message.team-loser-format"),
    NONE_FORMAT("game.game-end.stats-message.none-format"),
    END_GAME_STATS_FORMAT("game.game-end.stats-message.statistic-format"),
    END_GAME_MESSAGE("game.game-end.stats-message.message"),
    WINNER_TITLE("game.game-end.winner-team.title"),
    WINNER_SUBTITLE("game.game-end.winner-team.subtitle"),
    LOSER_TITLE("game.game-end.loser-team.title"),
    LOSER_SUBTITLE("game.game-end.loser-team.subtitle"),
    ROUND_WIN_TITLE("game.round-end.winner-team.title"),
    ROUND_WIN_SUBTITLE("game.round-end.winner-team.subtitle"),
    ROUND_LOSE_TITLE("game.round-end.loser-team.title"),
    ROUND_LOSE_SUBTITLE("game.round-end.loser-team.subtitle"),
    ROUND_DRAW_TITLE("game.round-end.draw.title"),
    ROUND_DRAW_SUBTITLE("game.round-end.draw.subtitle"),
    ROUND_START_COUNTDOWN_TITLE("game.round-start.countdown-title"),
    ROUND_START_COUNTDOWN_SUBTITLE("game.round-start.countdown-subtitle"),
    GAME_START_MESSAGE("game.start-message"),
    NO_STATS_ACHIEVED("game.round-end.stats-message.no-stats-achieved"),
    STATS_MESSAGE("game.round-end.stats-message.message"),
    STATS_KILLS("game.round-end.stats-message.kills-format"),
    STATS_PLACED_WOOL("game.round-end.stats-message.wool-placed-format"),
    STATS_BROKEN_BLOCKS("game.round-end.stats-message.blocks-broken-format"),
    PRE_ROUND_TITLE("game.pre-round.title"),
    PRE_ROUND_SUBTITLE("game.pre-round.subtitle"),
    ROUND_START_TITLE("game.round-start.title"),
    ROUND_START_SUBTITLE("game.round-start.subtitle"),
    SHIFT_TO_SELECT_KIT("game.pre-round.shift-to-select-kit"),
    GAME_START_COUNTDOWN("game.start-countdown"),
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
