package me.cubecrafter.woolwars.game.kits;

import com.cryptomorin.xseries.XPotion;
import lombok.Getter;
import me.cubecrafter.woolwars.game.arena.GameState;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
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
                String[] split = effect.split(",");
                PotionEffect potionEffect = new PotionEffect(XPotion.matchXPotion(split[0]).get().getPotionEffectType(), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                effects.add(potionEffect);
            }
        }
    }

    public void use(Player player) {
        if (!ArenaUtil.getArenaByPlayer(player).getGameState().equals(GameState.PLAYING)) {
            player.sendMessage(TextUtil.color("&cYou can't use your ability yet!"));
            return;
        }
        for (PotionEffect effect : effects) {
            player.addPotionEffect(effect);
        }
        player.sendMessage(TextUtil.color("&aAbility activated!"));
    }



}
