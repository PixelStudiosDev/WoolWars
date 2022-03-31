package me.cubecrafter.woolwars.commands;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import org.bukkit.command.CommandSender;

public class BaseCommand {

    @Command(name = "", desc = "Base WoolWars Command")
    public void baseCommand(@Sender CommandSender sender) {
        sender.sendMessage("help message");
    }

}
