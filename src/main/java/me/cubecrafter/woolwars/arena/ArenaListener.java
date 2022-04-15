package me.cubecrafter.woolwars.arena;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.Titles;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.config.ConfigPath;
import me.cubecrafter.woolwars.utils.GameUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ArenaListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        if (!GameUtil.isPlaying(player)) return;
        Arena arena = GameUtil.getArenaByPlayer(player);
        if (arena.getArenaRegion().isInside(e.getBlock().getLocation())) {
            arena.getPlacedBlocks().add(e.getBlock());
        }
        if (!arena.getGameState().equals(GameState.PLAYING)) return;
        if (arena.getBlocksRegion().isInside(e.getBlock().getLocation())) {
            Team team = arena.getTeamByPlayer(player);
            if (e.getBlock().getType().toString().contains("WOOL")) {
                e.getBlock().setMetadata("team", new FixedMetadataValue(WoolWars.getInstance(), team.getName()));
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
        if (arena.getBlocksRegion().isInside(e.getBlock().getLocation())) {
            if (e.getBlock().hasMetadata("team")) {
                String teamName = e.getBlock().getMetadata("team").get(0).asString();
                Team team = arena.getTeamByName(teamName);
                arena.getPlayingTask().removePlacedBlock(team);
            }
        } else if (!arena.getPlacedBlocks().contains(e.getBlock())) {
            e.setCancelled(true);
            player.sendMessage(TextUtil.color("&cYou can't break this block!"));
        } else {
            arena.getPlacedBlocks().remove(e.getBlock());
        }
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent e) {
        Player player = (Player) e.getEntity();
        if (GameUtil.getArenaByPlayer(player) != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        if (!GameUtil.isPlaying(player)) return;
        Arena arena = GameUtil.getArenaByPlayer(player);
        if (!arena.getGameState().equals(GameState.PLAYING) || arena.getDeadPlayers().contains(player)) {
            player.setFireTicks(0);
            player.setHealth(20);
            e.setCancelled(true);
            return;
        }
        if (e instanceof EntityDamageByEntityEvent) {
            if (!(((EntityDamageByEntityEvent) e).getDamager() instanceof Player)) return;
            Player damager = (Player) ((EntityDamageByEntityEvent) e).getDamager();
            if (arena.getDeadPlayers().contains(damager) || arena.isTeammate(player, damager)) {
                e.setCancelled(true);
                return;
            }
        }
        if (((player.getHealth() - e.getFinalDamage()) <= 0)) {
            if (e instanceof EntityDamageByEntityEvent) {
                if (!(((EntityDamageByEntityEvent) e).getDamager() instanceof Player)) return;
                Player damager = (Player) ((EntityDamageByEntityEvent) e).getDamager();
                Team playerTeam = arena.getTeamByPlayer(player);
                Team damagerTeam = arena.getTeamByPlayer(damager);
                arena.sendMessage(TextUtil.color(playerTeam.getTeamColor().getChatColor() + player.getName() + " &7was killed by " + damagerTeam.getTeamColor().getChatColor() + damager.getName()));
            }
            arena.getDeadPlayers().add(player);
            player.setFireTicks(0);
            player.setHealth(20);
            player.setAllowFlight(true);
            player.setFlying(true);
            player.spigot().setCollidesWithEntities(false);
            player.getInventory().setArmorContents(null);
            player.getInventory().clear();
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
            Titles.sendTitle(player, 0, 40, 0, TextUtil.color("&c&lYOU DIED"), TextUtil.color("&7You will respawn at the start of the next round!"));
            if (arena.getAlivePlayers().size() == 0) {
                arena.getBlocksRegion().clear();
                arena.getPlayingTask().getTask().cancel();
                arena.setGameState(GameState.PRE_ROUND);
            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        if (GameUtil.getArenas().stream().anyMatch(arena -> arena.getArenaRegion().isInside(e.getLocation()))) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (GameUtil.getArenaByPlayer(e.getPlayer()) == null) return;
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        for (String material : ConfigPath.DISABLED_INTERACTION_BLOCKS.getStringList()) {
            if (e.getClickedBlock().getType().equals(XMaterial.matchXMaterial(material).get().parseMaterial())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (!GameUtil.isPlaying(player)) return;
        Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        if (block.getType().equals(XMaterial.SLIME_BLOCK.parseMaterial())) {
            player.setVelocity(player.getLocation().getDirection().setY(2));
            player.setFallDistance(0.0F);
            XSound.play(player, "ENTITY_BAT_TAKEOFF");
        }
    }

}
