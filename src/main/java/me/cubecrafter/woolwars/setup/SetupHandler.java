package me.cubecrafter.woolwars.setup;

import lombok.experimental.UtilityClass;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.Arena;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * @author Infinity
 * 14-04-2022 / 04:58 PM
 * WoolWars / me.cubecrafter.woolwars.setup
 */

@UtilityClass
public class SetupHandler {

    private File arenaFile;
    private WoolWars instance = WoolWars.getInstance();
    private YamlConfiguration configuration;

    public void createArena(String arenaName, int maxTeamPlayers, int minPlayers) throws IOException {
        arenaFile = new File(WoolWars.getInstance().getDataFolder() + File.pathSeparator + "arenas", arenaName + ".yml");
        if (!arenaFile.exists()){
            instance.saveResource(arenaName + ".yml", false);
            configuration = YamlConfiguration.loadConfiguration(arenaFile);
        }
        Arena arena = new Arena(arenaName, configuration);
        configuration.set("displayname", arenaName);
        configuration.set("max-players-per-team", maxTeamPlayers);
        configuration.set("min-players", minPlayers);
        configuration.save(arenaFile);
    }
}
