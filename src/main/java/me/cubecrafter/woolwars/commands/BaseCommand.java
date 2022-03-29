package me.cubecrafter.woolwars.commands;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Require;
import com.jonahseguin.drink.annotation.Sender;
import org.bukkit.entity.Player;

/**
 * @author Infinity
 * 29-03-2022 / 11:23 PM
 * WoolWars / me.cubecrafter.woolwars.commands.subcommands
 */

public class BaseCommand {

    @Command(name = "", desc = "Command base")
    @Require(value = "woolwars.command.base")
    public void baseCommand(@Sender Player player){
        //send help message
    }

}
