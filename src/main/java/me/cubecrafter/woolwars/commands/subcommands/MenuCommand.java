package me.cubecrafter.woolwars.commands.subcommands;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import me.cubecrafter.woolwars.menu.menus.GameMenu;
import org.bukkit.entity.Player;

public class MenuCommand {

    @Command(name = "menu", desc = "Open menu")
    public void joinCommand(@Sender Player player) {
        new GameMenu(player).openMenu();
    }

}
