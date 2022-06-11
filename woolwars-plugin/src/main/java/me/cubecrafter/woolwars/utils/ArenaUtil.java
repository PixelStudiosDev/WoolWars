package me.cubecrafter.woolwars.utils;

import com.cryptomorin.xseries.XSound;
import lombok.experimental.UtilityClass;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.config.ConfigPath;
import me.cubecrafter.woolwars.database.PlayerData;
import me.cubecrafter.woolwars.game.arena.Arena;
import me.cubecrafter.woolwars.game.arena.ArenaState;
import me.cubecrafter.woolwars.game.kits.Kit;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ArenaUtil {

    public void teleportToLobby(Player player) {
        player.teleport(ConfigPath.LOBBY_LOCATION.getAsLocation());
    }

    public void showLobbyPlayers(Player player, Arena arena) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (arena.getPlayers().contains(online)) {
                player.hidePlayer(online);
                online.hidePlayer(player);
            } else {
                player.showPlayer(online);
                online.showPlayer(player);
            }
        }
    }

    public void hideDeadPlayer(Player player, Arena arena) {
        for (Player alive : arena.getAlivePlayers()) {
            alive.hidePlayer(player);
        }
        for (Player dead : arena.getDeadPlayers()) {
            player.showPlayer(dead);
        }
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
        return new ArrayList<>(WoolWars.getInstance().getArenaManager().getArenas().values());
    }

    public List<Kit> getKits() {
        return new ArrayList<>(WoolWars.getInstance().getKitManager().getKits().values());
    }

    public boolean joinRandom(Player player) {
        List<Arena> available = getArenas().stream().filter(arena -> arena.getArenaState().equals(ArenaState.WAITING) || arena.getArenaState().equals(ArenaState.STARTING)).collect(Collectors.toList());
        if (available.isEmpty()) {
            TextUtil.sendMessage(player, "&cThere are no available arenas!");
            return false;
        }
        Arena random = available.stream().max(Comparator.comparing(arena -> arena.getPlayers().size())).orElse(available.get(0));
        random.addPlayer(player);
        return true;
    }

    public boolean joinRandomFromGroup(Player player, String group) {
        List<Arena> available = getArenasByGroup(group).stream().filter(arena -> arena.getArenaState().equals(ArenaState.WAITING) || arena.getArenaState().equals(ArenaState.STARTING)).collect(Collectors.toList());
        if (available.isEmpty()) {
            TextUtil.sendMessage(player, "&cThere are no available " + group + " arenas!");
            return false;
        }
        Arena random = available.stream().max(Comparator.comparing(arena -> arena.getPlayers().size())).orElse(available.get(0));
        random.addPlayer(player);
        return true;
    }

    public void playSound(Player player, String sound) {
        XSound.play(player, sound);
    }

    public void playSound(List<Player> players, String sound) {
        players.forEach(player -> playSound(player, sound));
    }

}
