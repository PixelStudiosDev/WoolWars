package me.cubecrafter.woolwars.api.kits;

import me.cubecrafter.woolwars.api.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public interface Ability {

    /**
     * Get the kit of the ability
     * @return The kit of the ability
     */
    Kit getKit();

    /**
     * Get the ability name
     * @return The ability name
     */
    String getName();

    /**
     * Get the ability type
     * @return The ability type
     */
    AbilityType getAbilityType();

    /**
     * Get the ability item
     * @return The ability item
     */
    ItemStack getItem();

    /**
     * Get the inventory slot of the ability item
     * @return The inventory slot
     */
    int getItemSlot();

    /**
     * Get the ability effects
     * @return The ability effects
     */
    List<PotionEffect> getEffects();

    /**
     * Make a player use the ability
     * @param player The player
     * @param arena The arena
     */
    void use(Player player, Arena arena);

    enum AbilityType {
        EFFECT,
        KNOCKBACK_TNT,
        STEP_BACK,
        GOLDEN_SHELL,
        HACK
    }

}
