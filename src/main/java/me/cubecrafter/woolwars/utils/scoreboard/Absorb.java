/*
 * Copyright (c) 2019 Jonah Seguin.  All rights reserved.  You may not modify, decompile, distribute or use any code/text contained in this document(plugin) without explicit signed permission from Jonah Seguin.
 */

package me.cubecrafter.woolwars.utils.scoreboard;

import com.google.common.collect.Maps;
import lombok.Getter;
import me.cubecrafter.woolwars.utils.scoreboard.view.View;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Map;

/**
 * Created by Jonah on 11/4/2017.
 * Project: aBsorb
 *
 * @ 4:23 PM
 */
@Getter
public class Absorb {

    private static final Scoreboard BLANK_SCOREBOARD = Bukkit.getScoreboardManager().getNewScoreboard();
    private final Plugin plugin;
    private final Map<String, View> views = Maps.newConcurrentMap();
    private volatile View activeView = null;
    private final Scoreboard scoreboard;
    private final Objective objective;
    private final Player player;
    private boolean visible = true;

    public Absorb(Plugin plugin, Player player, boolean override) {
        this.plugin = plugin;
        this.player = player;
        if (override) {
            this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(scoreboard);
        }
        else {
            if (player.getScoreboard() != null) {
                this.scoreboard = player.getScoreboard();
            }
            else {
                this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                player.setScoreboard(scoreboard);
            }
        }

        Objective obj = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        if (obj == null) {
            obj = scoreboard.registerNewObjective(player.getName(), "dummy");
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            obj.setDisplayName(" ");
        }

        this.objective = obj;
    }

    public boolean isActive(String view) {
        return this.activeView != null && this.activeView.getName().equals(view);
    }

    public void activate(String viewName) {
        View view = getView(viewName);
        if (view != null) {
            this.activate(view);
        }
    }

    public void activate(View view) {
        if (this.activeView != null) {
            this.activeView.unrender();
        }
        this.activeView = view;
        this.activeView.render();
    }


    public void registerView(View view) {
        this.views.put(view.getName(), view);
    }

    public void unregisterView(View view) {
        this.views.remove(view.getName());
        if (this.activeView.getName().equals(view.getName())) {
            this.activeView = null;
            this.activeView = view();
        }
    }

    public View getView(String view) {
        return views.get(view);
    }

    public View newView(String name) {
        View view = new View(name, this);
        this.registerView(view);
        return view;
    }

    public View view(String name) {
        View view = getView(name);
        if (view != null) {
            return view;
        }
        else {
            return newView(name);
        }
    }

    public View view() {
        if (this.activeView != null) {
            return this.activeView;
        }
        return this.views.values().stream().findFirst().orElse(this.newView("default"));
    }

    public void show() {
        this.visible = true;
        this.player.setScoreboard(this.scoreboard);
    }

    public void hide() {
        this.visible = false;
        this.player.setScoreboard(BLANK_SCOREBOARD);
    }

}
