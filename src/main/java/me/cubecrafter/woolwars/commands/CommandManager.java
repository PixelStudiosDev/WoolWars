package me.cubecrafter.woolwars.commands;

import com.jonahseguin.drink.CommandService;
import com.jonahseguin.drink.Drink;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.commands.subcommands.*;

public class CommandManager {

    public CommandManager() {
        CommandService drink = Drink.get(WoolWars.getInstance());
        drink.register(new BaseCommand(), "woolwars", "ww")
                .registerSub(new JoinCommand())
                .registerSub(new LeaveCommand())
                .registerSub(new SetSpawnCommand())
                .registerSub(new MenuCommand())
                .registerSub(new TestCommand());
        drink.register(new LobbyCommand(), "lobby");
        drink.registerCommands();
    }

}
