package me.cubecrafter.woolwars.listeners;

import com.cryptomorin.xseries.XMaterial;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.api.arena.GameState;
import me.cubecrafter.woolwars.api.team.Team;
import me.cubecrafter.woolwars.arena.GameArena;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class WoolListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (!ArenaUtil.isPlaying(player)) return;
        GameArena arena = (GameArena) ArenaUtil.getArenaByPlayer(player);
        if (!arena.getGameState().equals(GameState.ACTIVE_ROUND) || arena.getDeadPlayers().contains(player)) {
            e.setCancelled(true);
            TextUtil.sendMessage(player, Messages.CANT_BREAK_BLOCK.getAsString());
        } else if (arena.getCenter().isInside(e.getBlock().getLocation())) {
            if (arena.isCenterLocked()) {
                TextUtil.sendMessage(player, "&cCenter is locked!");
                e.setCancelled(true);
                return;
            }
            if (e.getBlock().hasMetadata("woolwars")) {
                if (!e.getBlock().getMetadata("woolwars").isEmpty()) {
                    String teamName = e.getBlock().getMetadata("woolwars").get(0).asString();
                    Team team = arena.getTeamByName(teamName);
                    arena.getRoundTask().removePlacedWool(team);
                    e.getBlock().removeMetadata("woolwars", WoolWars.getInstance());
                }
            }
            e.getBlock().setType(Material.AIR);
            arena.getRoundTask().addBrokenBlock(player);
        } else if (!arena.getPlacedBlocks().contains(e.getBlock())) {
            e.setCancelled(true);
            TextUtil.sendMessage(player, Messages.CANT_BREAK_BLOCK.getAsString());
        } else {
            arena.getPlacedBlocks().remove(e.getBlock());
            e.getBlock().setType(Material.AIR);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        GameArena arena = (GameArena) ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        if (!arena.getGameState().equals(GameState.ACTIVE_ROUND) || !arena.getArenaRegion().isInside(e.getBlock().getLocation())) {
            e.setCancelled(true);
            TextUtil.sendMessage(player, Messages.CANT_PLACE_BLOCK.getAsString());
            return;
        }
        if (!ArenaUtil.isBlockInTeamBase(e.getBlock(), arena)) {
            if (e.getBlockAgainst().getType().toString().contains("LAVA") || e.getBlockAgainst().getType().toString().contains("WATER")) {
                e.setCancelled(true);
                TextUtil.sendMessage(player, Messages.CANT_PLACE_BLOCK.getAsString());
                return;
            }
            if (arena.getCenter().isInside(e.getBlock().getLocation())) {
                if (arena.isCenterLocked()) {
                    TextUtil.sendMessage(player, "&cCenter is locked!");
                    e.setCancelled(true);
                    return;
                }
                if (e.getBlock().getType().toString().endsWith("WOOL")) {
                    Team team = arena.getTeamByPlayer(player);
                    e.getBlock().setMetadata("woolwars", new FixedMetadataValue(WoolWars.getInstance(), team.getName()));
                    arena.getRoundTask().addPlacedWool(team);
                    arena.getRoundTask().addPlacedWool(player);
                    arena.getRoundTask().checkWinners();
                }
                return;
            }
            for (String material : Configuration.PLACEABLE_BLOCKS.getAsStringList()) {
                if (e.getBlock().getType().equals(XMaterial.matchXMaterial(material).get().parseMaterial())) {
                    arena.getPlacedBlocks().add(e.getBlock());
                    return;
                }
            }
            e.setCancelled(true);
            TextUtil.sendMessage(player, Messages.CANT_PLACE_BLOCK.getAsString());
        } else {
            e.setCancelled(true);
            TextUtil.sendMessage(player, Messages.CANT_PLACE_BLOCK.getAsString());
        }
    }

}
