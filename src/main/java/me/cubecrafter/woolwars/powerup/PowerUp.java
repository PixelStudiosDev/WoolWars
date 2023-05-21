/*
 * Wool Wars
 * Copyright (C) 2023 CubeCrafter Development
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.cubecrafter.woolwars.powerup;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.config.Config;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.xutils.TextUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class PowerUp {

    private static final String[] UPGRADABLE_ITEMS = {
            "_HELMET", "_CHESTPLATE", "_LEGGINGS", "_BOOTS",
            "_PICKAXE", "_SWORD", "_AXE", "_SHOVEL", "_HOE", "BOW"
    };

    private final Arena arena;
    private final Location location;
    private final List<ArmorStand> hologram = new ArrayList<>();

    private ArmorStand stand;
    private PowerUpData data;
    private int rotation;
    private boolean active;

    public void use(WoolPlayer player) {
        remove();
        player.playSound(Config.SOUNDS_POWERUP_COLLECTED.asString());
        // Add potion effects
        for (PotionEffect effect : data.getEffects()) {
            player.getPlayer().addPotionEffect(effect, true);
        }
        // Give items
        PlayerInventory inventory = player.getPlayer().getInventory();
        for (ItemStack item : data.getItems()) {
            String material = item.getType().toString();
            // Check if item is upgradable
            String type = Arrays.stream(UPGRADABLE_ITEMS).filter(material::endsWith).findAny().orElse(null);
            boolean found = false;
            if (type != null) {
                switch (type) {
                    case "_HELMET":
                        inventory.setHelmet(item);
                        found = true;
                        break;
                    case "_CHESTPLATE":
                        inventory.setChestplate(item);
                        found = true;
                        break;
                    case "_LEGGINGS":
                        inventory.setLeggings(item);
                        found = true;
                        break;
                    case "_BOOTS":
                        inventory.setBoots(item);
                        found = true;
                        break;
                    default:
                        // Find the item to replace
                        for (int i = 0; i < inventory.getSize(); i++) {
                            ItemStack stack = inventory.getItem(i);
                            if (stack == null || stack.getType() == Material.AIR) continue;
                            if (stack.getType().toString().endsWith(type)) {
                                inventory.setItem(i, item);
                                found = true;
                                break;
                            }
                        }
                        break;
                }
            }
            // If not upgradable, add to inventory
            if (!found) {
                inventory.addItem(item);
            }
        }
    }

    public void spawn() {
        data = WoolWars.get().getPowerupManager().getRandom();
        stand = spawnArmorStand(null, location);

        ItemStack item = data.getDisplayedItem();
        if (XMaterial.PLAYER_HEAD.parseMaterial().equals(item.getType())) {
            stand.getEquipment().setHelmet(item);
        } else {
            stand.getEquipment().setItemInHand(item);
            stand.setRightArmPose(new EulerAngle(Math.toRadians(280), Math.toRadians(270), 0));
        }

        setupHolo();
        active = true;
    }

    public void remove() {
        if (!active) return;
        active = false;
        stand.remove();
        hologram.forEach(Entity::remove);
        hologram.clear();
    }

    public void rotate() {
        if (!active) return;
        Location loc = stand.getLocation();
        loc.setYaw(rotation);
        stand.teleport(loc);
        rotation += 3;
    }

    private void setupHolo() {
        double offset = 2;
        List<String> reversed = new ArrayList<>(data.getHoloLines());
        Collections.reverse(reversed);
        for (String line : reversed) {
            hologram.add(spawnArmorStand(line, location.clone().add(0, offset, 0)));
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
