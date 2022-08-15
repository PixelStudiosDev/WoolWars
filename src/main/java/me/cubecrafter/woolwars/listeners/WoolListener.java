package me.cubecrafter.woolwars.listeners;

import com.cryptomorin.xseries.XMaterial;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.team.Team;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;

public class WoolListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (!ArenaUtil.isPlaying(player)) return;
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (!arena.getGameState().equals(GameState.ACTIVE_ROUND) || arena.getDeadPlayers().contains(player)) {
            e.setCancelled(true);
            TextUtil.sendMessage(player, Messages.CANT_BREAK_BLOCK.getAsString());
        } else if (arena.getCenter().isInside(e.getBlock().getLocation())) {
            if (arena.isCenterLocked()) {
                TextUtil.sendMessage(player, "&cCenter is locked!");
                e.setCancelled(true);
                return;
            }
            for (List<Block> blocks : arena.getRoundTask().getPlacedWool().values()) {
                blocks.remove(e.getBlock());
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
        Arena arena = ArenaUtil.getArenaByPlayer(player);
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
                    arena.getRoundTask().addPlacedWool(team, e.getBlock());
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
        }
        e.setCancelled(true);
        TextUtil.sendMessage(player, Messages.CANT_PLACE_BLOCK.getAsString());
    }

}
