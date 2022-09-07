package me.cubecrafter.woolwars.kits.ability;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.api.events.player.PlayerAbilityEvent;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HackAbility extends Ability {

    public HackAbility(ConfigurationSection section) {
        super(section);
    }

    @Override
    public void execute(Player player, Arena arena) {
        if (arena.isCenterLocked()) {
            TextUtil.sendMessage(player, section.getString("messages.center-already-locked"));
            return;
        }
        PlayerAbilityEvent event = new PlayerAbilityEvent(player, this, arena);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        TextUtil.sendMessage(player, Messages.ABILITY_USE.getAsString().replace("{name}", getDisplayName()));
        WoolWars.getInstance().getKitManager().addCooldown(player);
        arena.setCenterLocked(true);
        new BukkitRunnable() {
            double timer = section.getDouble("ability-duration");
            @Override
            public void run() {
                if (arena.getGameState() != GameState.ACTIVE_ROUND) {
                    arena.setCenterLocked(false);
                    cancel();
                    return;
                }
                if (timer <= 0) {
                    arena.setCenterLocked(false);
                    TextUtil.sendActionBar(arena.getPlayers(), section.getString("messages.center-unlocked"));
                    cancel();
                } else {
                    TextUtil.sendActionBar(arena.getPlayers(), section.getString("messages.center-locked").replace("{seconds}", String.format("%.1f", timer)));
                    timer -= 0.1;
                }
            }
        }.runTaskTimer(WoolWars.getInstance(), 2L, 2L);
    }

}
