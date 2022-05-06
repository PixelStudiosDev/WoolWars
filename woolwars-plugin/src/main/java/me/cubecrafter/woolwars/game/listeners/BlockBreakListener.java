package me.cubecrafter.woolwars.game.listeners;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.game.arena.GameState;
import me.cubecrafter.woolwars.game.arena.Arena;
import me.cubecrafter.woolwars.game.team.Team;
import me.cubecrafter.woolwars.utils.GameUtil;
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
        if (!GameUtil.isPlaying(player)) return;
        Arena arena = GameUtil.getArenaByPlayer(player);
        if (!arena.getGameState().equals(GameState.PLAYING) || arena.getDeadPlayers().contains(player)) {
            e.setCancelled(true);
            player.sendMessage(TextUtil.color("&cYou can't break this block!"));
        } else if (arena.getBlocksRegion().isInside(e.getBlock().getLocation())) {
            if (e.getBlock().hasMetadata("woolwars")) {
                if (!e.getBlock().getMetadata("woolwars").isEmpty()) {
                    String teamName = e.getBlock().getMetadata("woolwars").get(0).asString();
                    Team team = arena.getTeamByName(teamName);
                    arena.getPlayingTask().removePlacedWool(team);
                    e.getBlock().removeMetadata("woolwars", WoolWars.getInstance());
                }
            }
            e.getBlock().setType(Material.AIR);
        } else if (!arena.getPlacedBlocks().contains(e.getBlock())) {
            e.setCancelled(true);
            player.sendMessage(TextUtil.color("&cYou can't break this block!"));
        } else {
            arena.getPlacedBlocks().remove(e.getBlock());
            e.getBlock().setType(Material.AIR);
        }
    }

}
