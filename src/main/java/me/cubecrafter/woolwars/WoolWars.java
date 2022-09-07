package me.cubecrafter.woolwars;

import lombok.Getter;
import me.cubecrafter.woolwars.arena.ArenaManager;
import me.cubecrafter.woolwars.commands.CommandManager;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.config.FileManager;
import me.cubecrafter.woolwars.database.Database;
import me.cubecrafter.woolwars.database.PlayerDataManager;
import me.cubecrafter.woolwars.hooks.PlaceholderHook;
import me.cubecrafter.woolwars.kits.KitManager;
import me.cubecrafter.woolwars.listeners.ArenaListener;
import me.cubecrafter.woolwars.listeners.ChatListener;
import me.cubecrafter.woolwars.listeners.InventoryListener;
import me.cubecrafter.woolwars.listeners.JoinQuitListener;
import me.cubecrafter.woolwars.listeners.RewardsListener;
import me.cubecrafter.woolwars.listeners.ScoreboardHandler;
import me.cubecrafter.woolwars.listeners.WoolListener;
import me.cubecrafter.woolwars.powerup.PowerUpManager;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

@Getter
public final class WoolWars extends JavaPlugin {

    @Getter private static WoolWars instance;
    private ArenaManager arenaManager;
    private FileManager fileManager;
    private CommandManager commandManager;
    private Database storage;
    private KitManager kitManager;
    private ScoreboardHandler scoreboardHandler;
    private PlayerDataManager playerDataManager;
    private PowerUpManager powerupManager;

    @Override
    public void onEnable() {
        instance = this;
        TextUtil.info(" __      __        _  __      __           ");
        TextUtil.info(" \\ \\    / /__  ___| | \\ \\    / /_ _ _ _ ___");
        TextUtil.info("  \\ \\/\\/ / _ \\/ _ \\ |  \\ \\/\\/ / _` | '_(_-<");
        TextUtil.info("   \\_/\\_/\\___/\\___/_|   \\_/\\_/\\__,_|_| /__/");
        TextUtil.info("");
        TextUtil.info("Author: CubeCrafter");
        TextUtil.info("Version: " + getDescription().getVersion());
        TextUtil.info("Running on: " + getServer().getVersion());
        TextUtil.info("Java Version: " + System.getProperty("java.version"));
        fileManager = new FileManager(this);
        storage = new Database();
        arenaManager = new ArenaManager(this);
        commandManager = new CommandManager(this);
        playerDataManager = new PlayerDataManager();
        scoreboardHandler = new ScoreboardHandler();
        powerupManager = new PowerUpManager();
        kitManager = new KitManager(this);
        kitManager.load();
        Arrays.asList(new InventoryListener(), new ArenaListener(), new WoolListener(), new JoinQuitListener(this), new ChatListener())
                .forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
        registerHooks();
        if (Configuration.REWARD_COMMANDS_ENABLED.getAsBoolean()) {
            getServer().getPluginManager().registerEvents(new RewardsListener(), this);
        }
        playerDataManager.forceLoad();
        new Metrics(this, 14788);
    }

    @Override
    public void onDisable() {
        playerDataManager.forceSave();
        storage.close();
        arenaManager.disableArenas();
        scoreboardHandler.disable();
        getServer().getScheduler().cancelTasks(this);
    }

    private void registerHooks() {
        if (isPAPIEnabled()) {
            new PlaceholderHook().register();
            TextUtil.info("Hooked into PlaceholderAPI!");
        }
    }

    public boolean isPAPIEnabled() {
        return getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

}
