package me.cubecrafter.woolwars.utils;

import com.cryptomorin.xseries.XSound;
import lombok.experimental.UtilityClass;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.api.arena.Arena;
import me.cubecrafter.woolwars.api.arena.GameState;
import me.cubecrafter.woolwars.api.database.PlayerData;
import me.cubecrafter.woolwars.api.kits.Kit;
import me.cubecrafter.woolwars.arena.GameArena;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.config.Messages;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ArenaUtil {

    public void teleportToLobby(Player player) {
        if (Configuration.LOBBY_LOCATION.getAsString().equals("")) {
            TextUtil.sendMessage(player, "{prefix}&cThe lobby location is not set! Set it using /woolwars setlobby");
            return;
        }
        player.teleport(Configuration.LOBBY_LOCATION.getAsLocation());
    }

    public boolean isBlockInTeamBase(Block block, GameArena arena) {
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
        return new ArrayList<>(WoolWars.getInstance().getKitManager().getKits().values());
    }

    public boolean joinRandomArena(Player player) {
        List<Arena> available = getArenas().stream().filter(arena -> arena.getGameState().equals(GameState.WAITING) || arena.getGameState().equals(GameState.STARTING)).collect(Collectors.toList());
        if (available.isEmpty()) {
            TextUtil.sendMessage(player, Messages.NO_ARENAS_AVAILABLE.getAsString());
            return false;
        }
        Arena random = available.stream().max(Comparator.comparing(arena -> arena.getPlayers().size())).orElse(available.get(0));
        random.addPlayer(player);
        return true;
    }

    public boolean joinRandomArena(Player player, String group) {
        List<Arena> available = getArenasByGroup(group).stream().filter(arena -> arena.getGameState().equals(GameState.WAITING) || arena.getGameState().equals(GameState.STARTING)).collect(Collectors.toList());
        if (available.isEmpty()) {
            TextUtil.sendMessage(player, Messages.NO_ARENAS_AVAILABLE.getAsString());
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
