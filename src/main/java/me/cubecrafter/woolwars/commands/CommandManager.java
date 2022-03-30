package me.cubecrafter.woolwars.commands;

import com.jonahseguin.drink.CommandService;
import com.jonahseguin.drink.Drink;
import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.commands.subcommands.JoinCommand;
import me.cubecrafter.woolwars.commands.subcommands.LeaveCommand;

/**
 * @author Infinity
 * 29-03-2022 / 11:28 PM
 * WoolWars / me.cubecrafter.woolwars.commands
 */

public class CommandManager {

    @Getter private final CommandService drink;

    public CommandManager(WoolWars plugin) {
        drink = Drink.get(plugin);
        drink.register(new BaseCommand(), "woolwars", null)
                .registerSub(new JoinCommand())
                .registerSub(new LeaveCommand());
        drink.registerCommands();
    }

}
