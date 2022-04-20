package me.cubecrafter.woolwars.arena;

import com.cryptomorin.xseries.XSound;
import lombok.Getter;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PowerUp {

    private ArmorStand armorStand;
    private final List<ArmorStand> holoLines = new ArrayList<>();
    private final Location location;
    private final Arena arena;
    private final List<String> actions;
    private int rotation = 0;
    private boolean active = false;

    public PowerUp(Location location, Arena arena, List<String> actions) {
        this.arena = arena;
        this.location = location;
        this.actions = actions;
    }

    public void use(Player player) {
        remove();
        XSound.play(player, "ENTITY_PLAYER_LEVELUP");
        for (String line : actions) {
            if (!line.contains("[") || !line.contains("]")) continue;
            String type = line.substring(line.indexOf("[") + 1, line.indexOf("]")).toUpperCase();
            String other = line.substring(line.indexOf("]") + 1).replace(" ", "");
            TextUtil.info(type + ":" + other);
        }
    }

    public void spawn() {
        armorStand = spawnArmorStand(null, location);
        armorStand.getEquipment().setHelmet(new ItemBuilder("PLAYER_HEAD").setTexture("CubeCrafter72").build());
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
        rotation += 5;
    }

    private void setupHolo() {
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
