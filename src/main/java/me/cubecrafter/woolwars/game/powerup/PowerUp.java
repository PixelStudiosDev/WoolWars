package me.cubecrafter.woolwars.game.powerup;

import com.cryptomorin.xseries.XSound;
import lombok.Getter;
import me.cubecrafter.woolwars.game.arena.Arena;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PowerUp {

    private ArmorStand armorStand;
    private final List<ArmorStand> holoLines = new ArrayList<>();
    private final Location location;
    private final Arena arena;
    private final PowerUpData data;
    private int rotation = 0;
    private boolean active = false;

    public PowerUp(Location location, Arena arena) {
        this.arena = arena;
        this.location = location;
        this.data = PowerUpData.getRandom();
    }

    public void use(Player player) {
        remove();
        XSound.play(player, "ENTITY_PLAYER_LEVELUP");
        for (ItemStack item : data.getItems()) {
            player.getInventory().addItem(item);
        }
        for (PotionEffect effect : data.getEffects()) {
            player.addPotionEffect(effect);
        }
    }

    public void spawn() {
        armorStand = spawnArmorStand(null, location);
        armorStand.getEquipment().setHelmet(data.getDisplayedItem());
        setupHolo();
        active = true;
    }

    public void remove() {
        if (!active) return;
        active = false;
        armorStand.remove();
        for (ArmorStand stand : holoLines) {
            stand.remove();
        }
        holoLines.clear();
    }

    public void rotate() {
        if (!active) return;
        armorStand.setHeadPose(new EulerAngle(0, Math.toRadians(rotation), 0));
        rotation += 3;
    }

    private void setupHolo() {
        double offset = 2;
        for (String line : data.getHoloLines()) {
            holoLines.add(spawnArmorStand(line, location.clone().add(0, offset, 0)));
            offset += 0.3;
        }
    }

    private ArmorStand spawnArmorStand(String name, Location location) {
        ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        stand.setGravity(false);
        stand.setRemoveWhenFarAway(false);
        stand.setVisible(false);
        stand.setCanPickupItems(false);
        stand.setArms(false);
        stand.setBasePlate(false);
        stand.setMarker(true);
        if (name != null) {
            stand.setCustomName(TextUtil.color(name));
            stand.setCustomNameVisible(true);
        }
        return stand;
    }

}
