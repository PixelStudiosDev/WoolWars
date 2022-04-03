/*
 * Copyright (c) 2019 Jonah Seguin.  All rights reserved.  You may not modify, decompile, distribute or use any code/text contained in this document(plugin) without explicit signed permission from Jonah Seguin.
 */

package me.cubecrafter.woolwars.utils.scoreboard.view;

import lombok.Getter;
import me.cubecrafter.woolwars.utils.scoreboard.Absorb;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Jonah on 11/4/2017.
 * Project: aBsorb
 *
 * @ 4:23 PM
 */
@Getter
public class View {

    private final Absorb absorb;
    private final String name;
    private final ViewContext context;
    private final List<ViewEntry> entries = new ArrayList<>();
    private final Set<String> identifiers = new HashSet<>();

    private ViewProvider provider = null;

    public View(String name, Absorb absorb) {
        this.name = name;
        this.absorb = absorb;
        this.context = new ViewContext(absorb, this, absorb.getPlayer());
    }

    private static String getRandomChatColor() {
        return ChatColor.values()[ThreadLocalRandom.current().nextInt(ChatColor.values().length)].toString();
    }

    public View provider(ViewProvider provider) {
        this.provider = provider;
        return this;
    }

    public void render() {
        if (this.provider == null) return;

        Objective o = this.absorb.getObjective();

        String title = ChatColor.translateAlternateColorCodes('&', this.provider.getTitle(this.context));

        if (!o.getDisplayName().equals(title)) {
            o.setDisplayName(title);
        }

        List<String> newLines = this.provider.getLines(this.context);

        if (newLines == null || newLines.isEmpty()) {
            this.unrender();
        } else {
            Collections.reverse(newLines);

            if (this.entries.size() > newLines.size()) {
                for (int i = newLines.size(); i < this.entries.size(); i++) {
                    ViewEntry entry = getEntryAt(i);
                    if (entry != null) {
                        entry.remove();
                    }
                }
            }

            int x = 1;
            for (int i = 0; i < newLines.size(); i++) {
                ViewEntry entry = getEntryAt(i);

                String line = ChatColor.translateAlternateColorCodes('&', newLines.get(i));

                if (entry == null) {
                    entry = new ViewEntry(this, line);
                } else {
                    entry.setText(line);
                    entry.setup();
                }
                entry.render(x++);
            }

        }

        this.provider.onUpdate(this.context);
    }

    public void unrender() {
        this.entries.forEach(ViewEntry::remove);
        this.entries.clear();
        this.identifiers.clear();
        this.absorb.getScoreboard().getTeams().forEach(Team::unregister);
    }

    public boolean isActive() {
        if (absorb == null || absorb.getActiveView() == null) {
            return false;
        }
        return absorb.getActiveView().getName().equals(this.name);
    }

    public ViewEntry getEntryAt(int score) {
        if (score >= this.entries.size()) {
            return null;
        }
        return this.entries.get(score);
    }

    public String getUniqueID() {
        String id = getRandomChatColor() + ChatColor.WHITE;

        while (this.identifiers.contains(id)) {
            id = id + getRandomChatColor() + ChatColor.WHITE;
        }

        if (id.length() > 16) {
            return this.getUniqueID();
        }

        this.identifiers.add(id);

        return id;
    }

}
