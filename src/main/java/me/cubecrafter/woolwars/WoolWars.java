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
import me.cubecrafter.woolwars.listeners.PlayerQuitListener;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

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

        ConsoleCommandSender console = getServer().getConsoleSender();
        console.sendMessage(TextUtil.color("&c __      __        ___      __                       "));
        console.sendMessage(TextUtil.color("&c \\ \\    / /__  ___| \\ \\    / /_ _ _ _ ___   &7Author: &aCubeCrafter"));
        console.sendMessage(TextUtil.color("&c  \\ \\/\\/ / _ \\/ _ \\ |\\ \\/\\/ / _` | '_(_-<   &7Plugin version: &a" + getDescription().getVersion()));
        console.sendMessage(TextUtil.color("&c   \\_/\\_/\\___/\\___/_| \\_/\\_/\\__,_|_| /__/   &7Running on: &a" + getServer().getVersion()));
        console.sendMessage("");

        new Metrics(this, 14788);
        registerHooks();

        fileManager = new FileManager();
        SQLdatabase = new Database();
        gameManager = new GameManager();
        commandManager = new CommandManager();
        kitManager = new KitManager();
        playerDataHandler = new PlayerDataHandler();
        scoreboardHandler = new ScoreboardHandler();

        Arrays.asList(new MenuListener(), new InteractListener(), new ArenaListener(), new PlayerQuitListener())
                .forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
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
