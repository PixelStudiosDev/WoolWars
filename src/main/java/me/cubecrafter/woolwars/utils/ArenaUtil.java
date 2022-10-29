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

package me.cubecrafter.woolwars.utils;

import com.cryptomorin.xseries.XSound;
import lombok.experimental.UtilityClass;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.storage.PlayerData;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.kits.Kit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ArenaUtil {

    public void teleportToLobby(Player player) {
        if (Config.LOBBY_LOCATION.getAsString().equals("")) {
            TextUtil.sendMessage(player, "{prefix}&cThe lobby location is not set! Set it using /woolwars setlobby");
            return;
        }
        player.teleport(Config.LOBBY_LOCATION.getAsLocation());
    }

    public boolean isBlockInTeamBase(Block block, Arena arena) {
        return arena.getTeams().stream().anyMatch(team -> team.getBase().isInside(block.getLocation()));
    }

    public Arena getArenaByPlayer(Player player) {
        return getArenas().stream().filter(arena -> arena.getPlayers().contains(player)).findAny().orElse(null);
    }

    public Arena getArenaById(String name) {
        return WoolWars.getInstance().getArenaManager().getArena(name);
    }

    public List<Arena> getArenasByGroup(String group) {
        return getArenas().stream().filter(arena -> arena.getGroup().equals(group)).collect(Collectors.toList());
    }

    public List<String> getGroups() {
        return getArenas().stream().map(Arena::getGroup).distinct().collect(Collectors.toList());
    }

    public Kit getKit(String id) {
        return WoolWars.getInstance().getKitManager().getKit(id);
    }

    public Kit getKitByPlayer(Player player) {
        return WoolWars.getInstance().getKitManager().getKit(getPlayerData(player).getSelectedKit());
    }

    public PlayerData getPlayerData(Player player) {
        return WoolWars.getInstance().getPlayerDataManager().getPlayerData(player);
    }

    public boolean isPlaying(Player player) {
        return getArenas().stream().anyMatch(arena -> arena.getPlayers().contains(player));
    }

    public List<Arena> getArenas() {
        return WoolWars.getInstance().getArenaManager().getArenas();
    }

    public List<Kit> getKits() {
        return new ArrayList<>(WoolWars.getInstance().getKitManager().getKits());
    }

    public boolean joinRandomArena(Player player) {
        List<Arena> available = getArenas().stream().filter(arena -> arena.getGameState().equals(GameState.WAITING) || arena.getGameState().equals(GameState.STARTING)).collect(Collectors.toList());
        if (available.isEmpty()) {
            TextUtil.sendMessage(player, Messages.NO_ARENAS_AVAILABLE.getAsString());
            return false;
        }
        Arena random = available.stream().max(Comparator.comparing(arena -> arena.getPlayers().size())).orElse(available.get(0));
        random.addPlayer(player, true);
        return true;
    }

    public boolean joinRandomArena(Player player, String group) {
        List<Arena> available = getArenasByGroup(group).stream().filter(arena -> arena.getGameState().equals(GameState.WAITING) || arena.getGameState().equals(GameState.STARTING)).collect(Collectors.toList());
        if (available.isEmpty()) {
            TextUtil.sendMessage(player, Messages.NO_ARENAS_AVAILABLE.getAsString());
            return false;
        }
        Arena random = available.stream().max(Comparator.comparing(arena -> arena.getPlayers().size())).orElse(available.get(0));
        random.addPlayer(player, true);
        return true;
    }

    public void playSound(Player player, String sound) {
        XSound.play(player, sound);
    }

    public void playSound(List<Player> players, String sound) {
        players.forEach(player -> playSound(player, sound));
    }

    public void handleDeath(Player player, Arena arena) {
        ArenaUtil.playSound(arena.getPlayers(), Config.SOUNDS_PLAYER_DEATH.getAsString());
        arena.addDeaths(player, 1);
        PlayerData data = ArenaUtil.getPlayerData(player);
        data.setDeaths(data.getDeaths() + 1);
        arena.getDeadPlayers().add(player);
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.getInventory().setArmorContents(null);
        player.getInventory().clear();
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
        player.setFireTicks(0);
        player.setHealth(20);
        TextUtil.sendTitle(player, 2,  Messages.DEATH_TITLE.getAsString(), Messages.DEATH_SUBTITLE.getAsString());
        for (Player alive : arena.getAlivePlayers()) {
            VersionUtil.hidePlayer(alive, player);
        }
        for (Player dead : arena.getDeadPlayers()) {
            VersionUtil.showPlayer(player, dead);
        }
        ItemStack teleporter = ItemBuilder.fromConfig(Config.TELEPORTER_ITEM.getAsSection()).setTag("teleport-item").build();
        player.getInventory().setItem(Config.TELEPORTER_ITEM.getAsSection().getInt("slot"), teleporter);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 0, false, false));
        if (arena.getAlivePlayers().isEmpty()) {
            TextUtil.sendMessage(arena.getPlayers(),  Messages.ALL_PLAYERS_DEAD.getAsString());
            arena.getRoundTask().cancel();
            arena.setGameState(GameState.ROUND_OVER);
        }
    }

}
