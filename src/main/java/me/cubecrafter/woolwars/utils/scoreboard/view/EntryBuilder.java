/*
 * Copyright (c) 2019 Jonah Seguin.  All rights reserved.  You may not modify, decompile, distribute or use any code/text contained in this document(plugin) without explicit signed permission from Jonah Seguin.
 */

package me.cubecrafter.woolwars.utils.scoreboard.view;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class EntryBuilder {

    private final List<String> entries = new ArrayList<>();

    public EntryBuilder blank() {
        return next(" ");
    }

    public EntryBuilder next(String s) {
        this.entries.add(convert(s));
        return this;
    }

    public List<String> build() {
        return this.entries;
    }

    private String convert(String s) {
        if (s.length() > 48) {
            s = s.substring(0, 47);
        }
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
