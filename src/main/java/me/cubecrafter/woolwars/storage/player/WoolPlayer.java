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

package me.cubecrafter.woolwars.storage.player;

import lombok.Data;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.kit.Kit;
import me.cubecrafter.woolwars.arena.ArenaUtil;
import me.cubecrafter.woolwars.kit.KitManager;
import me.cubecrafter.woolwars.utils.Utils;
import me.cubecrafter.woolwars.utils.VersionUtil;
import me.cubecrafter.xutils.SoundUtil;
import me.cubecrafter.xutils.TextUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@Data
public class WoolPlayer {

    private final static KitManager kitManager = WoolWars.get().getKitManager();

    private final Player player;

    private PlayerData data = new PlayerData();
    private boolean alive;
    private boolean abilityUsed;

    public void send(String message) {
        if (message.startsWith("<center>") && message.endsWith("</center>")) {
            message = Utils.getCenteredMessage(message);
        }
        TextUtil.sendMessage(player, message);
    }

    public void send(List<String> messages) {
        messages.forEach(this::send);
    }

    public void teleportToLobby() {
        ArenaUtil.teleportToLobby(player);
    }

    public void setVisibility(WoolPlayer player, boolean visible) {
        if (visible) {
            VersionUtil.showPlayer(this.player, player.getPlayer());
        } else {
            VersionUtil.hidePlayer(this.player, player.getPlayer());
        }
    }

    public void setCollidable(boolean collidable) {
        VersionUtil.setCollidable(player, collidable);
    }

    public void teleport(Location location) {
        player.teleport(location);
    }

    public void playSound(String sound) {
        SoundUtil.play(player, sound);
    }

    public void sendActionBar(String message) {
        TextUtil.sendActionBar(player, message);
    }

    public void sendTitle(String title, String subtitle, int stay) {
        TextUtil.sendTitle(player, title, subtitle, 0, stay * 20, 0);
    }

    public Kit getSelectedKit() {
        return kitManager.getKit(data.getSelectedKit());
    }

    public void reset(GameMode mode) {
        alive = true;
        player.setGameMode(mode);
        player.setFoodLevel(20);
        player.setHealth(20);
        player.setFireTicks(0);
        player.getInventory().setArmorContents(null);
        player.getInventory().clear();
        player.setFlying(false);
        player.setAllowFlight(false);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
    }

    public String getName() {
        return player.getName();
    }

    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    public Location getLocation() {
        return player.getLocation();
    }

    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }

}
