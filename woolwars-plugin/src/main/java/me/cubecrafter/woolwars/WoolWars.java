package me.cubecrafter.woolwars;

import lombok.Getter;
import me.cubecrafter.woolwars.api.NMS;
import me.cubecrafter.woolwars.commands.CommandManager;
import me.cubecrafter.woolwars.config.FileManager;
import me.cubecrafter.woolwars.database.Database;
import me.cubecrafter.woolwars.database.PlayerDataManager;
import me.cubecrafter.woolwars.game.arena.ArenaManager;
import me.cubecrafter.woolwars.game.kits.KitManager;
import me.cubecrafter.woolwars.game.listeners.*;
import me.cubecrafter.woolwars.hooks.PlaceholderHook;
import me.cubecrafter.woolwars.hooks.VaultHook;
import me.cubecrafter.woolwars.utils.LicenseVerifier;
import me.cubecrafter.woolwars.utils.ScoreboardHandler;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bstats.bukkit.Metrics;
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
    private VaultHook vaultHook;
    private NMS nms;

    @Override
    public void onEnable() {
        instance = this;
        fileManager = new FileManager();
        if (!new LicenseVerifier(this, fileManager.getConfig().getString("license-key"), "http://142.132.151.133:1452/api/client", "565a2feab733667b66246aab765d03623fab8f1d").verify()) {
            getServer().getScheduler().cancelTasks(this);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        String version = getServer().getClass().getName().split("\\.")[3];
        try {
            Class<?> clazz = Class.forName("me.cubecrafter.woolwars.nms." + version);
            nms = (NMS) clazz.getConstructor().newInstance();
            TextUtil.info("Support for version " + version + " loaded!");
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
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
        kitManager = new KitManager();
        Arrays.asList(new MenuListener(), new InteractListener(), new ArenaListener(), new PlayerQuitListener(),
                        new PlayerJoinListener(), new BlockPlaceListener(), new BlockBreakListener(), new ChatListener())
                .forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
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
