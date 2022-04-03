/*
 * Copyright (c) 2019 Jonah Seguin.  All rights reserved.  You may not modify, decompile, distribute or use any code/text contained in this document(plugin) without explicit signed permission from Jonah Seguin.
 */

package me.cubecrafter.woolwars.utils.scoreboard.view;

import java.util.List;

public interface ViewProvider {

    String getTitle(ViewContext context);

    List<String> getLines(ViewContext context);

    default void onUpdate(ViewContext context) {}

}
