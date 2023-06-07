/*
 * Wool Wars
 * Copyright (C) 2023 CubeCrafter Development
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.cubecrafter.woolwars;

import lombok.Getter;
import lombok.Setter;
import me.cubecrafter.woolwars.arena.ArenaManager;
import me.cubecrafter.woolwars.arena.TabHandler;
import me.cubecrafter.woolwars.commands.WoolCommand;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.hooks.PlaceholderHook;
import me.cubecrafter.woolwars.kit.KitManager;
import me.cubecrafter.woolwars.listeners.ArenaListener;
import me.cubecrafter.woolwars.listeners.BlockListener;
import me.cubecrafter.woolwars.listeners.ChatListener;
import me.cubecrafter.woolwars.listeners.DamageListener;
import me.cubecrafter.woolwars.listeners.InteractListener;
import me.cubecrafter.woolwars.listeners.InventoryListener;
import me.cubecrafter.woolwars.listeners.JoinQuitListener;
import me.cubecrafter.woolwars.listeners.MoveListener;
import me.cubecrafter.woolwars.listeners.PlayerListener;
import me.cubecrafter.woolwars.listeners.RewardsListener;
import me.cubecrafter.woolwars.listeners.ScoreboardListener;
import me.cubecrafter.woolwars.listeners.SetupListener;
import me.cubecrafter.woolwars.listeners.StatisticsListener;
import me.cubecrafter.woolwars.party.PartyProvider;
import me.cubecrafter.woolwars.party.provider.PAFBungee;
import me.cubecrafter.woolwars.party.provider.PAFSpigot;
import me.cubecrafter.woolwars.powerup.PowerUpManager;
import me.cubecrafter.woolwars.storage.Database;
import me.cubecrafter.woolwars.storage.player.PlayerManager;
import me.cubecrafter.woolwars.storage.type.MySQL;
import me.cubecrafter.woolwars.storage.type.SQLite;
import me.cubecrafter.woolwars.utils.Utils;
import me.cubecrafter.xutils.Tasks;
import me.cubecrafter.xutils.TextUtil;
import me.cubecrafter.xutils.config.ConfigManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class WoolWars extends JavaPlugin {

    private static WoolWars instance;

    private ArenaManager arenaManager;
    private Database storage;
    private KitManager kitManager;
    private PlayerManager playerManager;
    private PowerUpManager powerupManager;
    private ScoreboardListener scoreboard;
    private TabHandler tabHandler;

    @Setter
    private PartyProvider partyProvider;

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

        storage = Config.MYSQL_ENABLED.asBoolean() ? new MySQL() : new SQLite();
        arenaManager = new ArenaManager(this);
        playerManager = new PlayerManager(this);
        kitManager = new KitManager(this);
        powerupManager = new PowerUpManager();
        scoreboard = new ScoreboardListener();
        tabHandler = new TabHandler();

        registerListeners();
        registerHooks();

        WoolCommand.register();

        arenaManager.load();
        kitManager.load();
        powerupManager.load();
        playerManager.load();

        Utils.checkForUpdates();
        new Metrics(this, 14788);
    }

    @Override
    public void onDisable() {
        arenaManager.disable();
        scoreboard.disable();
        playerManager.save();
        storage.close();

        Tasks.cancelAll();
    }

    private void registerListeners() {
        Listener[] listeners = {
                new ArenaListener(),
                new BlockListener(),
                new StatisticsListener(),
                new JoinQuitListener(),
                new DamageListener(),
                new MoveListener(),
                new InventoryListener(),
                new ChatListener(),
                new SetupListener(),
                new RewardsListener(),
                new InteractListener(),
                new PlayerListener()
        };
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    private void registerHooks() {
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderHook().register();
            TextUtil.info("Hooked into PlaceholderAPI!");
        }
        // Find a party provider
        if (getServer().getPluginManager().isPluginEnabled("Spigot-Party-API-PAF")) {
            partyProvider = new PAFBungee();
            TextUtil.info("Hooked into Party And Friends Bungeecord!");
        } else if (getServer().getPluginManager().isPluginEnabled("PartyAndFriends")) {
            partyProvider = new PAFSpigot();
            TextUtil.info("Hooked into Party And Friends Spigot!");
        }
    }

    public void reload() {
        ConfigManager.get().reloadAll();

        kitManager.load();
        powerupManager.load();
    }

    public static WoolWars get() {
        return instance;
    }

}
