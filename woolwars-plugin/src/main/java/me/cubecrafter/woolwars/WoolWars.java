package me.cubecrafter.woolwars;

import lombok.Getter;
import me.cubecrafter.woolwars.api.API;
import me.cubecrafter.woolwars.api.NMS;
import me.cubecrafter.woolwars.api.WoolWarsAPI;
import me.cubecrafter.woolwars.commands.CommandManager;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.config.FileManager;
import me.cubecrafter.woolwars.database.Database;
import me.cubecrafter.woolwars.database.PlayerDataManager;
import me.cubecrafter.woolwars.arena.ArenaManager;
import me.cubecrafter.woolwars.kits.KitManager;
import me.cubecrafter.woolwars.listeners.GameListener;
import me.cubecrafter.woolwars.listeners.BlockBreakListener;
import me.cubecrafter.woolwars.listeners.BlockPlaceListener;
import me.cubecrafter.woolwars.listeners.ChatListener;
import me.cubecrafter.woolwars.listeners.InventoryListener;
import me.cubecrafter.woolwars.listeners.PlayerJoinListener;
import me.cubecrafter.woolwars.listeners.PlayerQuitListener;
import me.cubecrafter.woolwars.powerup.PowerUpManager;
import me.cubecrafter.woolwars.hooks.PlaceholderHook;
import me.cubecrafter.woolwars.hooks.VaultHook;
import me.cubecrafter.woolwars.utils.Auth;
import me.cubecrafter.woolwars.listeners.ScoreboardHandler;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

@Getter
public final class WoolWars extends JavaPlugin {

    @Getter private static WoolWars instance;
    private ArenaManager arenaManager;
    private FileManager fileManager;
    private CommandManager commandManager;
    private Database SQLDatabase;
    private KitManager kitManager;
    private ScoreboardHandler scoreboardHandler;
    private PlayerDataManager playerDataManager;
    private PowerUpManager powerupManager;
    private VaultHook vaultHook;
    private NMS nms;
    private WoolWarsAPI api;
    private final String user = "%%__USER__%%";

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
        if (!new Auth(this, Configuration.LICENSE_KEY.getAsString(), "http://142.132.151.133:1452/api/client", "565a2feab733667b66246aab765d03623fab8f1d").verify()) {
            getServer().getScheduler().cancelTasks(this);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        String version = getServer().getClass().getName().split("\\.")[3];
        try {
            Class<?> clazz = Class.forName("me.cubecrafter.woolwars.nms." + version);
            nms = (NMS) clazz.getConstructor(Plugin.class).newInstance(this);
            TextUtil.info("Support for version " + version + " loaded!");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            TextUtil.severe("Your server version (" + version + ") is not supported! Disabling...");
            getServer().getScheduler().cancelTasks(this);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        new Metrics(this, 14788);
        registerHooks();
        SQLDatabase = new Database();
        arenaManager = new ArenaManager();
        commandManager = new CommandManager();
        playerDataManager = new PlayerDataManager();
        scoreboardHandler = new ScoreboardHandler();
        powerupManager = new PowerUpManager();
        kitManager = new KitManager();
        Arrays.asList(new InventoryListener(), new GameListener(), new PlayerQuitListener(), new PlayerJoinListener(),
                        new BlockPlaceListener(), new BlockBreakListener(), new ChatListener())
                .forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
        playerDataManager.forceLoad();
        api = new API();
        getServer().getServicesManager().register(WoolWarsAPI.class, api, this, ServicePriority.Highest);
    }

    @Override
    public void onDisable() {
        playerDataManager.forceSave();
        SQLDatabase.close();
        arenaManager.disableArenas();
        scoreboardHandler.disable();
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
