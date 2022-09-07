package me.cubecrafter.woolwars.kits.ability;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.api.events.player.PlayerAbilityEvent;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class CustomAbility extends Ability {

    public CustomAbility(ConfigurationSection section) {
        super(section);
    }

    @Override
    public void execute(Player player, Arena arena) {
        PlayerAbilityEvent event = new PlayerAbilityEvent(player, this, arena);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        TextUtil.sendMessage(player, Messages.ABILITY_USE.getAsString().replace("{name}", getDisplayName()));
        WoolWars.getInstance().getKitManager().addCooldown(player);
        List<String> actions = section.getStringList("actions");
        for (String action : actions) {
            if (!action.startsWith("[") || !action.contains("]")) continue;
            String type = action.substring(1, action.indexOf("]"));
            String args = action.substring(action.indexOf("]") + 1).trim();
            switch (type.toUpperCase()) {
                case "CONSOLE":
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), args.replace("{player}", player.getName()));
                    break;
                case "COMMAND":
                    Bukkit.dispatchCommand(player, args.replace("{player}", player.getName()));
                    break;
                case "MESSAGE":
                    TextUtil.sendMessage(player, args.replace("{player}", player.getName()));
                    break;
                case "EFFECT":
                    player.addPotionEffect(TextUtil.getEffect(args));
                    break;
                case "SOUND":
                    ArenaUtil.playSound(player, args);
                    break;
            }
        }
    }

}
