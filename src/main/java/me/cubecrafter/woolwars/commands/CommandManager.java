package me.cubecrafter.woolwars.commands;

import com.jonahseguin.drink.CommandService;
import com.jonahseguin.drink.Drink;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.commands.subcommands.JoinCommand;
import me.cubecrafter.woolwars.commands.subcommands.LeaveCommand;
import me.cubecrafter.woolwars.commands.subcommands.SetSpawnCommand;
import me.cubecrafter.woolwars.commands.subcommands.LobbyCommand;

public class CommandManager {

    public CommandManager() {
        CommandService drink = Drink.get(WoolWars.getInstance());
        drink.register(new BaseCommand(), "woolwars", "ww")
                .registerSub(new JoinCommand())
                .registerSub(new LeaveCommand())
                .registerSub(new SetSpawnCommand());
        drink.register(new LobbyCommand(), "lobby");
        drink.registerCommands();
    }

}
