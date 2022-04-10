package me.cubecrafter.woolwars;

import lombok.Getter;
import me.cubecrafter.woolwars.commands.CommandManager;
import me.cubecrafter.woolwars.config.FileManager;
import me.cubecrafter.woolwars.core.ArenaListener;
import me.cubecrafter.woolwars.core.GameManager;
import me.cubecrafter.woolwars.core.KitManager;
import me.cubecrafter.woolwars.core.PlayerDataHandler;
import me.cubecrafter.woolwars.core.ScoreboardHandler;
import me.cubecrafter.woolwars.database.Database;
import me.cubecrafter.woolwars.hooks.PlaceholderHook;
import me.cubecrafter.woolwars.hooks.VaultHook;
import me.cubecrafter.woolwars.libs.bstats.Metrics;
import me.cubecrafter.woolwars.listeners.InteractListener;
import me.cubecrafter.woolwars.listeners.MenuListener;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.plugin.java.JavaPlugin;

public final class WoolWars extends JavaPlugin {

    @Getter private static WoolWars instance;
    @Getter private GameManager gameManager;
    @Getter private FileManager fileManager;
    @Getter private CommandManager commandManager;
    @Getter private Database SQLdatabase;
    @Getter private PlayerDataHandler playerDataHandler;
    @Getter private KitManager kitManager;
    @Getter private ScoreboardHandler scoreboardHandler;
    @Getter private VaultHook vaultHook;

    @Override
    public void onEnable() {
        instance = this;

        TextUtil.info("\n" +
                " __          __         ___          __            \n" +
                " \\ \\        / /        | \\ \\        / /            \n" +
                "  \\ \\  /\\  / /__   ___ | |\\ \\  /\\  / /_ _ _ __ ___ \n" +
                "   \\ \\/  \\/ / _ \\ / _ \\| | \\ \\/  \\/ / _` | '__/ __|\n" +
                "    \\  /\\  / (_) | (_) | |  \\  /\\  / (_| | |  \\__ \\\n" +
                "     \\/  \\/ \\___/ \\___/|_|   \\/  \\/ \\__,_|_|  |___/\n" +
                "                                                          \n");

        new Metrics(this, 14788);
        registerHooks();

        fileManager = new FileManager();
        SQLdatabase = new Database();
        gameManager = new GameManager();
        commandManager = new CommandManager();
        kitManager = new KitManager();
        playerDataHandler = new PlayerDataHandler();
        scoreboardHandler = new ScoreboardHandler();

        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new InteractListener(), this);
        getServer().getPluginManager().registerEvents(new ArenaListener(), this);
    }

    @Override
    public void onDisable() {
        SQLdatabase.close();
        getServer().getScheduler().cancelTasks(this);
    }

    private void registerHooks() {
        if (isPAPIEnabled()) {
            new PlaceholderHook().register();
            TextUtil.info("Hooked into PlaceholderAPI!");
        }
        if (isVaultEnabled()) {
            vaultHook = new VaultHook();
            TextUtil.info("Hooked into Vault!");
        }
    }

    public boolean isPAPIEnabled() {
        return getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public boolean isVaultEnabled() {
        return getServer().getPluginManager().isPluginEnabled("Vault");
    }

}
