package me.cubecrafter.woolwars.game.kits;

import com.cryptomorin.xseries.XPotion;
import lombok.Getter;
import me.cubecrafter.woolwars.game.arena.GamePhase;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;

@Getter
public class Ability {

    private final AbilityType abilityType;
    private final ItemStack item;
    private final List<PotionEffect> effects = new ArrayList<>();

    public Ability(YamlConfiguration kitConfig) {
        abilityType = AbilityType.valueOf(kitConfig.getString("ability.type"));
        item = new ItemBuilder("BLAZE_POWDER").setDisplayName("&eKeystone Ability").setTag("ability-item").build();
        if (abilityType.equals(AbilityType.EFFECT)) {
            for (String effect : kitConfig.getStringList("ability.effects")) {
                effects.add(TextUtil.getEffect(effect));
            }
        }
    }

    public void use(Player player) {
        if (!ArenaUtil.getArenaByPlayer(player).getGamePhase().equals(GamePhase.ACTIVE_ROUND)) {
            player.sendMessage(TextUtil.color("&cYou can't use your ability yet!"));
            return;
        }
        for (PotionEffect effect : effects) {
            player.addPotionEffect(effect);
        }
        if (abilityType.equals(AbilityType.TNT)) {
            TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(player.getLocation(), EntityType.PRIMED_TNT);
            tnt.setIsIncendiary(false);
            tnt.setVelocity(player.getEyeLocation().getDirection().multiply(1.5));
        }
        player.sendMessage(TextUtil.color("&aAbility activated!"));
    }



}
