package me.cubecrafter.woolwars.kits.ability;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.api.events.player.PlayerAbilityEvent;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GigaHealAbility extends Ability {

    public GigaHealAbility(ConfigurationSection section) {
        super(section);
    }

    @Override
    public void execute(Player player, Arena arena) {
        PlayerAbilityEvent event = new PlayerAbilityEvent(player, this, arena);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        TextUtil.sendMessage(player, Messages.ABILITY_USE.getAsString().replace("{name}", getDisplayName()));
        WoolWars.getInstance().getKitManager().addCooldown(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, section.getInt("effect.duration") * 20, section.getInt("effect.amplifier")));
    }

}

