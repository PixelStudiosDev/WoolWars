package me.cubecrafter.woolwars.arena;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PowerUp {

    private final ArmorStand armorStand;
    private final List<ArmorStand> holoLines = new ArrayList<>();
    private final Location location;
    private final Arena arena;
    private BukkitTask rotateTask;
    private int rotation = 0;

    public PowerUp(Location location, Arena arena) {
        this.arena = arena;
        this.location = location;
        armorStand = spawnArmorStand(null, location);
        armorStand.getEquipment().setHelmet(new ItemBuilder("PLAYER_HEAD").setTexture("CubeCrafter72").build());
        setupHolo();
        rotate();
    }

    public void remove() {
        rotateTask.cancel();
        for (ArmorStand stand : holoLines) {
            stand.remove();
        }
        armorStand.remove();
    }

    public void rotate() {
        rotateTask = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), () -> {
            armorStand.setHeadPose(new EulerAngle(0, Math.toRadians(rotation), 0));
            rotation += 5;
        }, 0L, 1L);
    }

    public void setupHolo() {
        holoLines.add(spawnArmorStand("&e&lPOWER UP", location.clone().add(0, 2, 0)));
        holoLines.add(spawnArmorStand("&d&lEXCLUSIVE", location.clone().add(0, 2.3, 0)));
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
