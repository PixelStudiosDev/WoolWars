package me.cubecrafter.woolwars;

import lombok.Getter;
import me.cubecrafter.woolwars.commands.CommandManager;
import me.cubecrafter.woolwars.config.FileManager;
import me.cubecrafter.woolwars.core.GameManager;
import me.cubecrafter.woolwars.libs.Metrics;
import me.cubecrafter.woolwars.utils.RandomUtil;
import org.bukkit.plugin.java.JavaPlugin;

public final class WoolWars extends JavaPlugin {

    @Getter private static WoolWars instance;
    @Getter private GameManager gameManager;
    @Getter private FileManager fileManager;
    @Getter private CommandManager commandManager;

    @Override
    public void onEnable() {

        instance = this;
        new Metrics(this, 14788);

        fileManager = new FileManager();
        gameManager = new GameManager();

        RandomUtil.loadDependency();

        commandManager = new CommandManager(this);
        commandManager.load();
    }
}
