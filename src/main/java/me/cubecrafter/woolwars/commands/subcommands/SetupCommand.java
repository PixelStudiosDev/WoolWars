package me.cubecrafter.woolwars.commands.subcommands;

import me.cubecrafter.woolwars.arena.setup.SetupSession;
import me.cubecrafter.woolwars.commands.SubCommand;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetupCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (ArenaUtil.getArenaByPlayer(player) != null) {
            TextUtil.sendMessage(player, "{prefix}&cYou can't setup an arena while you're in game!");
            return;
        }
        if (args.length < 2) {
            if (SetupSession.isActive(player)) {
                SetupSession.getSession(player).getMenu().openMenu();
            } else {
                TextUtil.sendMessage(player, "{prefix}&cUsage: /woolwars setup <arena-id>");
            }
            return;
        }
        if (SetupSession.isActive(player)) {
            TextUtil.sendMessage(player, "{prefix}&cYou are already in setup mode!");
            return;
        }
        String id = args[1];
        if (ArenaUtil.getArenaById(id) != null) {
            TextUtil.sendMessage(player, "{prefix}&cAn arena called &e" + id + "&c already exists!");
            return;
        }
        new SetupSession(player, id);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public String getLabel() {
        return "setup";
    }

    @Override
    public String getPermission() {
        return "woolwars.admin";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

}
