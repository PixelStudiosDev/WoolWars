package me.cubecrafter.woolwars.listeners;

import com.cryptomorin.xseries.XMaterial;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.arena.GameArena;
import me.cubecrafter.woolwars.api.arena.GameState;
import me.cubecrafter.woolwars.team.GameTeam;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        GameArena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        if (!arena.getGameState().equals(GameState.ACTIVE_ROUND) || !arena.getArenaRegion().isInside(e.getBlock().getLocation())) {
            e.setCancelled(true);
            player.sendMessage(TextUtil.color("&cYou can't place blocks here!"));
            return;
        }
        if (!ArenaUtil.isBlockInTeamBase(e.getBlock(), arena)) {
            if (e.getBlockAgainst().getType().toString().contains("LAVA") || e.getBlockAgainst().getType().toString().contains("WATER")) {
                e.setCancelled(true);
                player.sendMessage(TextUtil.color("&cYou can't place blocks here!"));
                return;
            }
            if (arena.getWoolRegion().isInside(e.getBlock().getLocation())) {
                if (arena.isCenterLocked()) {
                    TextUtil.sendMessage(player, "&cCenter is locked!");
                    e.setCancelled(true);
                    return;
                }
                if (e.getBlock().getType().toString().endsWith("WOOL")) {
                    GameTeam team = arena.getTeamByPlayer(player);
                    e.getBlock().setMetadata("woolwars", new FixedMetadataValue(WoolWars.getInstance(), team.getName()));
                    arena.getPlayingTask().addPlacedWool(team);
                    arena.getPlayingTask().addPlacedWool(player);
                    arena.getPlayingTask().checkWinners();
                }
                return;
            }
            for (String material : Configuration.PLACEABLE_BLOCKS.getAsStringList()) {
                if (e.getBlock().getType().equals(XMaterial.matchXMaterial(material).get().parseMaterial())) {
                    arena.getArenaPlacedBlocks().add(e.getBlock());
                    return;
                }
            }
            e.setCancelled(true);
            player.sendMessage(TextUtil.color("&cYou can't place this block here!"));
        } else {
            e.setCancelled(true);
            player.sendMessage(TextUtil.color("&cYou can't place blocks here!"));
        }
    }

}


