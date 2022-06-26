package me.cubecrafter.woolwars.api.kits;

import me.cubecrafter.woolwars.api.team.Team;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface Kit {

    /**
     * Get the kit id
     * @return The kit id
     */
    String getId();

    /**
     * Get the kit displayname
     * @return The kit displayname
     */
    String getDisplayName();

    /**
     * Check if the helmet is enabled
     * @return True if the helmet is enabled, false otherwise
     */
    boolean isHelmetEnabled();

    /**
     * Check if the chestplate is enabled
     * @return True if the chestplate is enabled, false otherwise
     */
    boolean isChestplateEnabled();

    /**
     * Check if the leggings are enabled
     * @return True if the leggings are enabled, false otherwise
     */
    boolean isLeggingsEnabled();

    /**
     * Check if the boots are enabled
     * @return True if the boots are enabled, false otherwise
     */
    boolean isBootsEnabled();

    /**
     * Get the kit contents
     * @return The kit contents
     */
    Map<Integer, ItemStack> getContents();

    /**
     * Get the kit ability
     * @return The kit ability
     */
    Ability getAbility();

    /**
     * Add the kit to a player
     * @param player The player
     * @param team The player team
     */
    void addToPlayer(Player player, Team team);

}
