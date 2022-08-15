package me.cubecrafter.woolwars.api;

import lombok.experimental.UtilityClass;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.database.PlayerData;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.kits.Kit;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

@UtilityClass
public class WoolWarsAPI {

    public Location getLobbyLocation() {
        return Configuration.LOBBY_LOCATION.getAsLocation();
    }

    public List<Arena> getArenas() {
        return ArenaUtil.getArenas();
    }

    public Arena getArenaByPlayer(Player player) {
        return ArenaUtil.getArenaByPlayer(player);
    }

    public Arena getArenaById(String id) {
        return ArenaUtil.getArenaById(id);
    }

    public List<Arena> getArenasByGroup(String group) {
        return ArenaUtil.getArenasByGroup(group);
    }

    public List<String> getGroups() {
        return ArenaUtil.getGroups();
    }

    public Kit getKitById(String id) {
        return ArenaUtil.getKit(id);
    }

    public List<Kit> getKits() {
        return ArenaUtil.getKits();
    }

    public PlayerData getPlayerData(Player player) {
        return ArenaUtil.getPlayerData(player);
    }

    public boolean isPlaying(Player player) {
        return ArenaUtil.isPlaying(player);
    }

    public void joinRandomArena(Player player) {
        ArenaUtil.joinRandomArena(player);
    }

    public void joinRandomArena(Player player, String group) {
        ArenaUtil.joinRandomArena(player, group);
    }

}
