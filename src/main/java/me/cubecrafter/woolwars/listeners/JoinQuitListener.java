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

package me.cubecrafter.woolwars.listeners;

import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.api.events.player.PlayerLeaveArenaEvent;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.ArenaUtil;
import me.cubecrafter.woolwars.storage.player.PlayerManager;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.woolwars.utils.Utils;
import me.cubecrafter.woolwars.utils.VersionUtil;
import me.cubecrafter.xutils.Tasks;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class JoinQuitListener implements Listener {

    private final WoolWars plugin;

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) return;
        plugin.getPlayerManager().loadPlayer(event.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Disable join message
        event.setJoinMessage("");
        ArenaUtil.teleportToLobby(player);
        plugin.getTabHandler().onJoin(player);
        // Hide players in game
        Tasks.later(() -> {
            for (Player online : Bukkit.getOnlinePlayers()) {
                WoolPlayer woolPlayer = PlayerManager.get(online);
                if (ArenaUtil.isPlaying(woolPlayer)) {
                    VersionUtil.hidePlayer(player, online);
                    VersionUtil.hidePlayer(online, player);
                }
            }
        }, 15L);
        // Send update message
        if (Utils.isUpdateAvailable() && (player.isOp() || player.hasPermission("woolwars.admin"))) {
            sendUpdateMessage(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // Disable quit message
        event.setQuitMessage("");
        // Remove player from arena when they leave the server
        WoolPlayer woolPlayer = PlayerManager.get(player);
        Arena arena = ArenaUtil.getArenaByPlayer(woolPlayer);
        if (arena != null) {
            arena.removePlayer(woolPlayer, PlayerLeaveArenaEvent.Reason.DISCONNECT);
        }
        // Unload player
        plugin.getPlayerManager().unloadPlayer(player);
    }

    public void sendUpdateMessage(Player player) {
        HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8Click to open!").create());

        ComponentBuilder builder = new ComponentBuilder("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬\n");
        builder.append("§7A new version of §cWool Wars §7is available! (§e" + Utils.getLatestVersion() + "§7)\n         ");
        builder.append("§6SpigotMC").event(hover)
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/105548"));
        builder.append(" §7- §aPolymart").event(hover)
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://polymart.org/r/2551"));
        builder.append(" §7- §bBuiltByBit\n").event(hover)
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://builtbybit.com/resources/25971"));
        builder.append("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬")
                .event((ClickEvent) null).event((HoverEvent) null);

        player.spigot().sendMessage(builder.create());
    }

}
