package me.cubecrafter.woolwars.utils;

import com.cryptomorin.xseries.XSound;
import lombok.experimental.UtilityClass;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.api.arena.GameState;
import me.cubecrafter.woolwars.api.database.PlayerData;
import me.cubecrafter.woolwars.arena.GameArena;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.kits.Kit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ArenaUtil {

    public void teleportToLobby(Player player) {
        player.teleport(Configuration.LOBBY_LOCATION.getAsLocation());
    }

    public boolean isBlockInTeamBase(Block block, GameArena arena) {
        return arena.getTeams().stream().anyMatch(team -> team.getBase().isInside(block.getLocation()));
    }

    public GameArena getArenaByPlayer(Player player) {
        return getArenas().stream().filter(arena -> arena.getPlayers().contains(player)).findAny().orElse(null);
    }

    public GameArena getArenaById(String name) {
        return WoolWars.getInstance().getArenaManager().getArena(name);
    }

    public List<GameArena> getArenasByGroup(String group) {
        return getArenas().stream().filter(arena -> arena.getGroup().equals(group)).collect(Collectors.toList());
    }

    public List<String> getGroups() {
        return getArenas().stream().map(GameArena::getGroup).distinct().collect(Collectors.toList());
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

    public List<GameArena> getArenas() {
        return new ArrayList<>(WoolWars.getInstance().getArenaManager().getArenas().values());
    }

    public List<Kit> getKits() {
        return new ArrayList<>(WoolWars.getInstance().getKitManager().getKits().values());
    }

    public boolean joinRandom(Player player) {
        List<GameArena> available = getArenas().stream().filter(arena -> arena.getGameState().equals(GameState.WAITING) || arena.getGameState().equals(GameState.STARTING)).collect(Collectors.toList());
        if (available.isEmpty()) {
            TextUtil.sendMessage(player, Messages.NO_ARENAS_AVAILABLE.getAsString());
            return false;
        }
        GameArena random = available.stream().max(Comparator.comparing(arena -> arena.getPlayers().size())).orElse(available.get(0));
        random.addPlayer(player);
        return true;
    }

    public boolean joinRandomFromGroup(Player player, String group) {
        List<GameArena> available = getArenasByGroup(group).stream().filter(arena -> arena.getGameState().equals(GameState.WAITING) || arena.getGameState().equals(GameState.STARTING)).collect(Collectors.toList());
        if (available.isEmpty()) {
            TextUtil.sendMessage(player, Messages.NO_ARENAS_AVAILABLE.getAsString());
            return false;
        }
        GameArena random = available.stream().max(Comparator.comparing(arena -> arena.getPlayers().size())).orElse(available.get(0));
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
