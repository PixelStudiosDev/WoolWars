package me.cubecrafter.woolwars;

import me.cubecrafter.woolwars.commands.CommandManager;
import me.cubecrafter.woolwars.config.FileManager;
import me.cubecrafter.woolwars.core.GameManager;
import me.cubecrafter.woolwars.hooks.PlaceholderHook;
import me.cubecrafter.woolwars.libs.Metrics;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.plugin.java.JavaPlugin;

public final class WoolWars extends JavaPlugin {

    private static WoolWars instance;
    private GameManager gameManager;
    private FileManager fileManager;

    @Override
    public void onEnable() {
        instance = this;
        new Metrics(this, 14788);
        fileManager = new FileManager();
        gameManager = new GameManager();
        getCommand("woolwars").setExecutor(new CommandManager());
        getCommand("woolwars").setTabCompleter(new CommandManager());
        if (isPAPIEnabled()) {
            new PlaceholderHook().register();
            TextUtil.info("Hooked into PlaceholderAPI!");
        }
        if (isVaultEnabled()) {
            // TODO Vault Hook
            TextUtil.info("Hooked into Vault!");
        }
    }

    public boolean isPAPIEnabled() {
        return getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public boolean isVaultEnabled() {
        return getServer().getPluginManager().isPluginEnabled("Vault");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static WoolWars getInstance() {
        return instance;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

}
