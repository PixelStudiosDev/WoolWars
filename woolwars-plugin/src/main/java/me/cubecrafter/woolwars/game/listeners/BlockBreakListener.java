package me.cubecrafter.woolwars.game.listeners;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.api.game.arena.GameState;
import me.cubecrafter.woolwars.game.arena.Arena;
import me.cubecrafter.woolwars.game.team.Team;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (!ArenaUtil.isPlaying(player)) return;
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (!arena.getGameState().equals(GameState.ACTIVE_ROUND) || arena.getDeadPlayers().contains(player)) {
            e.setCancelled(true);
            player.sendMessage(TextUtil.color("&cYou can't break this block!"));
        } else if (arena.getWoolRegion().isInside(e.getBlock().getLocation())) {
            if (arena.isCenterLocked()) {
                TextUtil.sendMessage(player, "&cCenter is locked!");
                e.setCancelled(true);
                return;
            }
            if (e.getBlock().hasMetadata("woolwars")) {
                if (!e.getBlock().getMetadata("woolwars").isEmpty()) {
                    String teamName = e.getBlock().getMetadata("woolwars").get(0).asString();
                    Team team = arena.getTeamByName(teamName);
                    arena.getPlayingTask().removePlacedWool(team);
                    e.getBlock().removeMetadata("woolwars", WoolWars.getInstance());
                }
            }
            e.getBlock().setType(Material.AIR);
            arena.getPlayingTask().addBrokenBlock(player);
        } else if (!arena.getArenaPlacedBlocks().contains(e.getBlock())) {
            e.setCancelled(true);
            player.sendMessage(TextUtil.color("&cYou can't break this block!"));
        } else {
            arena.getArenaPlacedBlocks().remove(e.getBlock());
            e.getBlock().setType(Material.AIR);
        }
    }

}
