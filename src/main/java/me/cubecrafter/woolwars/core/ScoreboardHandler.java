package me.cubecrafter.woolwars.core;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.scoreboard.Absorb;
import me.cubecrafter.woolwars.utils.scoreboard.view.View;
import me.cubecrafter.woolwars.utils.scoreboard.view.ViewUpdater;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ScoreboardHandler implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Absorb absorb = new Absorb(WoolWars.getInstance(), e.getPlayer(), true);
        View view = absorb.view("scoreboard");
        view.provider(new ScoreboardAdapter());
        absorb.activate("scoreboard");
        absorb.show();
        ViewUpdater viewUpdater = new ViewUpdater(WoolWars.getInstance(), 20L); // Update every 2 ticks
        if (!viewUpdater.isRunning()) {
            viewUpdater.start();
        }
        viewUpdater.registerBoard(absorb);
    }

}
