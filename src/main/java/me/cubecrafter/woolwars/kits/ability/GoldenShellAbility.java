package me.cubecrafter.woolwars.kits.ability;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.api.events.player.PlayerAbilityEvent;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GoldenShellAbility extends Ability {

    public GoldenShellAbility(ConfigurationSection section) {
        super(section);
    }

    @Override
    public void execute(Player player, Arena arena) {
        PlayerAbilityEvent event = new PlayerAbilityEvent(player, this, arena);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        TextUtil.sendMessage(player, Messages.ABILITY_USE.getAsString().replace("{name}", getDisplayName()));
        WoolWars.getInstance().getKitManager().addCooldown(player);
        ItemStack[] armor = player.getInventory().getArmorContents();
        player.getInventory().setHelmet(new ItemBuilder("GOLDEN_HELMET").build());
        player.getInventory().setChestplate(new ItemBuilder("GOLDEN_CHESTPLATE").build());
        player.getInventory().setLeggings(new ItemBuilder("GOLDEN_LEGGINGS").build());
        player.getInventory().setBoots(new ItemBuilder("GOLDEN_BOOTS").build());
        Bukkit.getScheduler().runTaskLater(WoolWars.getInstance(), () -> {
            if (arena.getGameState() != GameState.ACTIVE_ROUND) return;
            player.getInventory().setArmorContents(armor);
        }, section.getInt("ability-duration") * 20L);
    }

}

