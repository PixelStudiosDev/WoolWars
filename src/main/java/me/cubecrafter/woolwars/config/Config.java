/*
 * Wool Wars
 * Copyright (C) 2022 CubeCrafter Development
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.cubecrafter.woolwars.config;

import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

@RequiredArgsConstructor
public enum Config {

    // GAME SETTINGS

    ENABLE_LEAVE_COMMAND_SHORTCUT("enable-leave-command-shortcut"),
    STARTING_COUNTDOWN("starting-countdown"),
    PRE_ROUND_DURATION("pre-round-duration"),
    ACTIVE_ROUND_DURATION("active-round-duration"),
    ROUND_OVER_DURATION("round-over-duration"),
    GAME_END_DURATION("game-end-duration"),
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

    // CHAT SETTINGS

    BLOCKED_COMMANDS("blocked-commands.commands"),
    BLOCKED_COMMANDS_WHITELIST("blocked-commands.whitelist"),
    CHAT_FORMAT_ENABLED("chat-format.enabled"),
    LOBBY_CHAT_FORMAT("chat-format.lobby-format"),
    WAITING_LOBBY_CHAT_FORMAT("chat-format.waiting-lobby-format"),
    GAME_CHAT_FORMAT("chat-format.game-format"),
    SPECTATOR_CHAT_FORMAT("chat-format.spectator-format"),

    // REWARDS

    REWARD_COMMANDS_ENABLED("reward-commands.enabled"),
    REWARD_COMMANDS_ROUND_WIN("reward-commands.round-win"),
    REWARD_COMMANDS_ROUND_LOSE("reward-commands.round-lose"),
    REWARD_COMMANDS_MATCH_WIN("reward-commands.match-win"),
    REWARD_COMMANDS_MATCH_LOSE("reward-commands.match-lose"),
    REWARD_COMMANDS_KILL("reward-commands.kill"),
    REWARD_COMMANDS_DEATH("reward-commands.death"),

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
    SOUNDS_COUNTDOWN("sounds.countdown"),
    SOUNDS_PLAYER_JOINED("sounds.player-joined"),
    SOUNDS_PLAYER_LEFT("sounds.player-left"),

    // SCOREBOARD & NAME TAGS SETTINGS

    SCOREBOARD_LOBBY_ENABLED("scoreboard.lobby-enabled"),
    SCOREBOARD_GAME_ENABLED("scoreboard.game-enabled"),
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

    public boolean getAsBoolean() {
        return WoolWars.getInstance().getFileManager().getConfig().getBoolean(path);
    }

    public Location getAsLocation() {
        return TextUtil.deserializeLocation(getAsString());
    }

    public ConfigurationSection getAsSection() {
        return WoolWars.getInstance().getFileManager().getConfig().getConfigurationSection(path);
    }

}
