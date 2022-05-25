package me.cubecrafter.woolwars.game.listeners;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.game.arena.Arena;
import me.cubecrafter.woolwars.game.arena.GamePhase;
import me.cubecrafter.woolwars.game.team.Team;
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
        if (!ArenaUtil.isPlaying(player)) return;
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (!arena.getGamePhase().equals(GamePhase.ACTIVE_ROUND)) {
            e.setCancelled(true);
            player.sendMessage(TextUtil.color("&cYou can't place blocks here!"));
        } else if (arena.getWoolRegion().isInside(e.getBlock().getLocation())) {
            Team team = arena.getTeamByPlayer(player);
            if (e.getBlock().getType().toString().contains("WOOL")) {
                e.getBlock().setMetadata("woolwars", new FixedMetadataValue(WoolWars.getInstance(), team.getName()));
                arena.getPlayingTask().addPlacedWool(team);
                arena.getPlayingTask().addPlacedWool(player);
                arena.getPlayingTask().checkWinners();
            }
        } else if (arena.getArenaRegion().isInside(e.getBlock().getLocation()) && !ArenaUtil.isBlockInTeamBase(e.getBlock(), arena) && e.getBlock().getType().toString().contains("GLASS")) {
            arena.getArenaPlacedBlocks().add(e.getBlock());
        } else {
            e.setCancelled(true);
            player.sendMessage(TextUtil.color("&cYou can't place blocks here!"));
        }
    }

}


