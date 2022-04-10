package me.cubecrafter.woolwars.core;

import com.cryptomorin.xseries.messages.Titles;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.utils.GameUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ArenaListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        if (!GameUtil.isPlaying(player)) return;
        Arena arena = GameUtil.getArenaByPlayer(player);
        if (!arena.getGameState().equals(GameState.PLAYING)) return;
        if (arena.getBlocksRegion().isInside(e.getBlock().getLocation()) && GameUtil.isPlaying(player)) {
            Team team = arena.getTeamByPlayer(player);
            if (e.getBlock().getType().toString().contains("WOOL")) {
                e.getBlock().setMetadata("team", new FixedMetadataValue(WoolWars.getInstance(), team.getName()));
                player.sendMessage(team.getName() + ": scored");
                arena.getPlayingTask().addPlacedBlock(team);
                arena.getPlayingTask().checkWinners();
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (!GameUtil.isPlaying(player)) return;
        Arena arena = GameUtil.getArenaByPlayer(player);
        if (arena.getBlocksRegion().isInside(e.getBlock().getLocation()) && GameUtil.isPlaying(player)) {
            if (e.getBlock().getType().toString().contains("WOOL") && e.getBlock().hasMetadata("team")) {
                String teamName = e.getBlock().getMetadata("team").get(0).asString();
                Team team = arena.getTeamByName(teamName);
                player.sendMessage(team.getName() + ": meno uno");
                arena.getPlayingTask().removePlacedBlock(team);
            }
        }
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent e) {
        Player player = (Player) e.getEntity();
        if (GameUtil.isPlaying(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        if (!GameUtil.isPlaying(player)) return;
        Arena arena = GameUtil.getArenaByPlayer(player);
        if (!arena.getGameState().equals(GameState.PLAYING)) {
            e.setCancelled(true);
            return;
        }
        if (((player.getHealth() - e.getFinalDamage()) <= 0)) {
            arena.getDeadPlayers().add(player);
            player.setAllowFlight(true);
            player.setFlying(true);
            player.spigot().setCollidesWithEntities(false);
            player.getInventory().setArmorContents(null);
            player.getInventory().clear();
            player.setHealth(20L);
            player.setFireTicks(0);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
            Titles.sendTitle(player, 0, 40, 0, TextUtil.color("&c&lYOU DIED"), TextUtil.color("&7You will respawn at the start of the next round!"));
            if (e instanceof EntityDamageByEntityEvent) {
                Player damager = (Player) ((EntityDamageByEntityEvent) e).getDamager();
                if (arena.getDeadPlayers().contains(damager) || arena.isTeammate(player, damager)) {
                    e.setCancelled(true);
                    return;
                }
                Team playerTeam = arena.getTeamByPlayer(player);
                Team damagerTeam = arena.getTeamByPlayer(damager);
                arena.sendMessage(TextUtil.color(playerTeam.getTeamColor().getChatColor() + player.getName() + " &7was killed by " + damagerTeam.getTeamColor().getChatColor() + damager.getName()));
            }
        }
    }

}
