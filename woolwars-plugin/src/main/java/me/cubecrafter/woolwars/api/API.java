package me.cubecrafter.woolwars.api;

import me.cubecrafter.woolwars.api.arena.Arena;
import me.cubecrafter.woolwars.api.database.PlayerData;
import me.cubecrafter.woolwars.api.kits.Kit;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class API implements WoolWarsAPI {

    @Override
    public Location getLobbyLocation() {
        return Configuration.LOBBY_LOCATION.getAsLocation();
    }

    @Override
    public List<Arena> getArenas() {
        return ArenaUtil.getArenas();
    }

    @Override
    public Arena getArenaByPlayer(Player player) {
        return ArenaUtil.getArenaByPlayer(player);
    }

    @Override
    public Arena getArenaById(String id) {
        return ArenaUtil.getArenaById(id);
    }

    @Override
    public List<Arena> getArenasByGroup(String group) {
        return ArenaUtil.getArenasByGroup(group);
    }

    @Override
    public List<String> getGroups() {
        return ArenaUtil.getGroups();
    }

    @Override
    public Kit getKitById(String id) {
        return ArenaUtil.getKit(id);
    }

    @Override
    public List<Kit> getKits() {
        return ArenaUtil.getKits();
    }

    @Override
    public PlayerData getPlayerData(Player player) {
        return ArenaUtil.getPlayerData(player);
    }

    @Override
    public boolean isPlaying(Player player) {
        return ArenaUtil.isPlaying(player);
    }

    @Override
    public void joinRandomArena(Player player) {
        ArenaUtil.joinRandomArena(player);
    }

    @Override
    public void joinRandomArena(Player player, String group) {
        ArenaUtil.joinRandomArena(player, group);
    }

}
