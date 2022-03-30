package me.cubecrafter.woolwars;

import lombok.Getter;
import me.cubecrafter.woolwars.commands.CommandManager;
import me.cubecrafter.woolwars.config.FileManager;
import me.cubecrafter.woolwars.core.GameManager;
import me.cubecrafter.woolwars.database.MongoManager;
import me.cubecrafter.woolwars.libs.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class WoolWars extends JavaPlugin {

    @Getter private static WoolWars instance;
    @Getter private GameManager gameManager;
    @Getter private FileManager fileManager;
    @Getter private CommandManager commandManager;
    @Getter private MongoManager mongoManager;

    @Override
    public void onEnable() {

        instance = this;
        new Metrics(this, 14788);

        mongoManager = new MongoManager();
        mongoManager.load();

        fileManager = new FileManager();
        gameManager = new GameManager();

        commandManager = new CommandManager(this);
        commandManager.load();
    }
}
