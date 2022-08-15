package me.cubecrafter.woolwars.powerup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class PowerUpData {

    private final ItemStack displayedItem;
    private final List<String> holoLines;
    private final List<ItemStack> items;
    private final List<PotionEffect> effects;

}
