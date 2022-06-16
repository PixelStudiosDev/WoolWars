package me.cubecrafter.woolwars.config;

import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

@RequiredArgsConstructor
public enum Configuration {

    LICENSE_KEY("license-key"),

    // GAME SETTINGS

    ENABLE_LEAVE_COMMAND_SHORTCUT("enable-leave-command-shortcut"),
    STARTING_COUNTDOWN("starting-countdown"),
    PRE_ROUND_COUNTDOWN("pre-round-countdown"),
    ACTIVE_ROUND_COUNTDOWN("active-round-countdown"),
    ROUND_OVER_COUNTDOWN("round-over-countdown"),
    GAME_ENDED_COUNTDOWN("game-ended-countdown"),
    DISABLED_INTERACTION_BLOCKS("disabled-interaction-blocks"),
    PLACEABLE_BLOCKS("placeable-blocks"),
    DISABLE_FALL_DAMAGE("disable-fall-damage"),
    PLAY_AGAIN_ITEM("items.play-again-item"),
    LEAVE_ITEM("items.leave-item"),
    TELEPORTER_ITEM("items.teleporter-item"),
    JUMP_PADS_TOP_BLOCK("jump-pads.top-block"),
    JUMP_PADS_BOTTOM_BLOCK("jump-pads.bottom-block"),
    JUMP_PADS_HORIZONTAL_POWER("jump-pads.horizontal-power"),
    JUMP_PADS_VERTICAL_POWER("jump-pads.vertical-power"),
    JUMP_PADS_PARTICLES_ENABLED("jump-pads.particles.enabled"),
    JUMP_PADS_PARTICLES_TYPE("jump-pads.particles.type"),

    // CHAT SETTINGS

    BLOCKED_COMMANDS("blocked-commands.commands"),
    BLOCKED_COMMANDS_WHITELIST("blocked-commands.whitelist"),
    CHAT_FORMAT_ENABLED("chat-format.enabled"),
    LOBBY_CHAT_FORMAT("chat-format.lobby-format"),
    WAITING_LOBBY_CHAT_FORMAT("chat-format.waiting-lobby-format"),
    GAME_CHAT_FORMAT("chat-format.game-format"),
    SPECTATOR_CHAT_FORMAT("chat-format.spectator-format"),

    // SOUNDS

    SOUNDS_JUMP_PAD("sounds.jump-pad"),
    SOUNDS_ROUND_START("sounds.round-start"),
    SOUNDS_ROUND_WON("sounds.round-won"),
    SOUNDS_ROUND_LOST("sounds.round-lost"),
    SOUNDS_ROUND_DRAW("sounds.round-draw"),
    SOUNDS_TELEPORT_TO_BASE("sounds.teleport-to-base"),
    SOUNDS_GAME_WON("sounds.game-won"),
    SOUNDS_GAME_LOST("sounds.game-lost"),
    SOUNDS_POWERUP_COLLECTED("sounds.powerup-collected"),
    SOUNDS_PLAYER_DEATH("sounds.player-death"),
    SOUNDS_PLAYER_KILL("sounds.player-kill"),
    SOUNDS_COUNTDOWN("sounds.countdown"),
    SOUNDS_PLAYER_JOINED("sounds.player-joined"),
    SOUNDS_PLAYER_LEFT("sounds.player-left"),

    // SCOREBOARD & NAME TAGS SETTINGS

    SCOREBOARD_ENABLED("scoreboard.enabled"),
    SCOREBOARD_REFRESH_INTERVAL("scoreboard.refresh-interval"),
    NAME_TAGS_ENABLED("name-tags.enabled"),
    NAME_TAGS_PREFIX("name-tags.prefix"),

    // STORAGE SETTINGS

    MYSQL_ENABLED("mysql.enabled"),
    MYSQL_HOST("mysql.host"),
    MYSQL_PORT("mysql.port"),
    MYSQL_DATABASE("mysql.database"),
    MYSQL_USERNAME("mysql.username"),
    MYSQL_PASSWORD("mysql.password"),
    MYSQL_SSL_ENABLED("mysql.use-ssl"),
    LOBBY_LOCATION("lobby-location");

    private final String path;

    public String getAsString() {
        return WoolWars.getInstance().getFileManager().getConfig().getString(path);
    }

    public int getAsInt() {
        return WoolWars.getInstance().getFileManager().getConfig().getInt(path);
    }

    public double getAsDouble() {
        return WoolWars.getInstance().getFileManager().getConfig().getDouble(path);
    }

    public List<String> getAsStringList() {
        return WoolWars.getInstance().getFileManager().getConfig().getStringList(path);
    }

    public List<Integer> getAsIntegerList() {
        return WoolWars.getInstance().getFileManager().getConfig().getIntegerList(path);
    }

    public boolean getAsBoolean() {
        return WoolWars.getInstance().getFileManager().getConfig().getBoolean(path);
    }

    public Location getAsLocation() {
        return TextUtil.deserializeLocation(getAsString());
    }

    public ConfigurationSection getAsConfigSection() {
        return WoolWars.getInstance().getFileManager().getConfig().getConfigurationSection(path);
    }

}
