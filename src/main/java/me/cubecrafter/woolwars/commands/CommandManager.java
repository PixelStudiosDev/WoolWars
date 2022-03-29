package me.cubecrafter.woolwars.commands;

import com.jonahseguin.drink.CommandService;
import com.jonahseguin.drink.Drink;
import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.commands.subcommands.JoinCommand;

/**
 * @author Infinity
 * 29-03-2022 / 11:28 PM
 * WoolWars / me.cubecrafter.woolwars.commands
 */

public class CommandManager {

    @Getter
    private static CommandService drink;

    public CommandManager(WoolWars instance) {
        drink = Drink.get(instance);
    }

    public void load() {
        drink.register(new BaseCommand(), "woolwars", null)
                .registerSub(new JoinCommand());

        drink.registerCommands();
    }
}
