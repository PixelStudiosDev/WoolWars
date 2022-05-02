package me.cubecrafter.woolwars.game.powerup;

import com.cryptomorin.xseries.XPotion;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
@RequiredArgsConstructor
public class PowerUpData {

    private static final List<PowerUpData> powerUps = new ArrayList<>();

    static {
        YamlConfiguration config = WoolWars.getInstance().getFileManager().getPowerUps();
        for (String id : config.getKeys(false)) {
            String displayedMaterial = config.getString(id + ".displayed-item.material");
            String texture = config.getString(id + ".displayed-item.texture");
            ItemStack displayedItem = new ItemBuilder(displayedMaterial).setTexture(texture).build();
            List<String> holoLines = config.getStringList(id + ".hologram-lines");
            List<ItemStack> items = new ArrayList<>();
            for (String item : config.getStringList(id + ".items")) {
                ItemStack created = new ItemBuilder(item).build();
                items.add(created);
            }
            List<PotionEffect> effects = new ArrayList<>();
            for (String effect : config.getStringList(id + ".effects")) {
                PotionEffect created = new PotionEffect(XPotion.matchXPotion(effect).get().getPotionEffectType(), 15, 1, false, false);
                effects.add(created);
            }
            PowerUpData data = new PowerUpData(displayedItem, holoLines, items, effects);
            powerUps.add(data);
        }
    }

    public static PowerUpData getRandom() {
        return powerUps.get(new Random().nextInt(powerUps.size()));
    }

    @Getter private final ItemStack displayedItem;
    @Getter private final List<String> holoLines;
    @Getter private final List<ItemStack> items;
    @Getter private final List<PotionEffect> effects;

}
