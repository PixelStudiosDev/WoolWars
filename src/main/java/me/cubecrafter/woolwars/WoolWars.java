package me.cubecrafter.woolwars;

import me.cubecrafter.woolwars.commands.CommandManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class WoolWars extends JavaPlugin {

    private static WoolWars instance;

    @Override
    public void onEnable() {
        instance = this;
        getCommand("woolwars").setExecutor(new CommandManager());
        getCommand("woolwars").setTabCompleter(new WoolWars());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static WoolWars getInstance() {
        return instance;
    }

}
