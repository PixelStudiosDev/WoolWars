package me.cubecrafter.woolwars.commands.subcommands;

import com.cryptomorin.xseries.XSound;
import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import org.bukkit.entity.Player;

public class TestCommand {

    @Command(name = "test", desc = "test")
    public void joinCommand(@Sender Player player, String sound) {
        XSound.play(player, sound);
    }

}
