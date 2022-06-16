package me.cubecrafter.woolwars.commands.subcommands;

import me.cubecrafter.woolwars.commands.SubCommand;
import me.cubecrafter.woolwars.arena.GameArena;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ForceStartCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        GameArena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        arena.forceStart();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public String getLabel() {
        return "forcestart";
    }

    @Override
    public String getPermission() {
        return "woolwars.forcestart";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

}
