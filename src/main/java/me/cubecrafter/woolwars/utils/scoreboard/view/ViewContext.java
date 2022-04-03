/*
 * Copyright (c) 2019 Jonah Seguin.  All rights reserved.  You may not modify, decompile, distribute or use any code/text contained in this document(plugin) without explicit signed permission from Jonah Seguin.
 */

package me.cubecrafter.woolwars.utils.scoreboard.view;

import lombok.Data;
import me.cubecrafter.woolwars.utils.scoreboard.Absorb;
import org.bukkit.entity.Player;

/**
 * Created by Jonah on 11/4/2017.
 * Project: aBsorb
 *
 * @ 4:40 PM
 */
@Data
public class ViewContext {

    private final Absorb absorb;
    private final View view;
    private final Player player;

}
