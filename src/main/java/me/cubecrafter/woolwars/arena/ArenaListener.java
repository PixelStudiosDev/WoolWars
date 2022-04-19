package me.cubecrafter.woolwars.arena;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.Titles;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.config.ConfigPath;
import me.cubecrafter.woolwars.utils.GameUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
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
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

public class ArenaListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        if (!GameUtil.isPlaying(player)) return;
        Arena arena = GameUtil.getArenaByPlayer(player);
        if (arena.getBlocksRegion().isInside(e.getBlock().getLocation()) && arena.getGameState().equals(GameState.PLAYING)) {
            Team team = arena.getTeamByPlayer(player);
            if (e.getBlock().getType().toString().contains("WOOL")) {
                e.getBlock().setMetadata("team", new FixedMetadataValue(WoolWars.getInstance(), team.getName()));
                arena.getPlayingTask().addPlacedBlock(team);
                arena.getPlayingTask().checkWinners();
            }
        } else if (arena.getArenaRegion().isInside(e.getBlock().getLocation())) {
            if (e.getBlock().getType().toString().contains("GLASS")) {
                arena.getPlacedBlocks().add(e.getBlock());
            } else {
                e.setCancelled(true);
                player.sendMessage(TextUtil.color("&cYou can't place blocks here!"));
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (!GameUtil.isPlaying(player)) return;
        Arena arena = GameUtil.getArenaByPlayer(player);
        if (arena.getDeadPlayers().contains(player)) {
            e.setCancelled(true);
            player.sendMessage(TextUtil.color("&cYou can't break this block!"));
            return;
        }
        if (arena.getBlocksRegion().isInside(e.getBlock().getLocation())) {
            if (e.getBlock().hasMetadata("team")) {
                String teamName = e.getBlock().getMetadata("team").get(0).asString();
                Team team = arena.getTeamByName(teamName);
                arena.getPlayingTask().removePlacedBlock(team);
                e.getBlock().setType(Material.AIR);
            }
        } else if (!arena.getPlacedBlocks().contains(e.getBlock())) {
            e.setCancelled(true);
            player.sendMessage(TextUtil.color("&cYou can't break this block!"));
        } else {
            arena.getPlacedBlocks().remove(e.getBlock());
            e.getBlock().setType(Material.AIR);
        }
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent e) {
        Player player = (Player) e.getEntity();
        if (GameUtil.getArenaByPlayer(player) != null) {
            e.setCancelled(true);
        }
    }

    private final Set<Player> jumping = new HashSet<>();

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        if (!GameUtil.isPlaying(player)) return;
        if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL) && jumping.contains(player)) {
            e.setCancelled(true);
            jumping.remove(player);
            return;
        }
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
            e.setCancelled(true);
            if (e instanceof EntityDamageByEntityEvent) {
                if (!(((EntityDamageByEntityEvent) e).getDamager() instanceof Player)) return;
                Player damager = (Player) ((EntityDamageByEntityEvent) e).getDamager();
                Team playerTeam = arena.getTeamByPlayer(player);
                Team damagerTeam = arena.getTeamByPlayer(damager);
                arena.sendMessage(TextUtil.color(playerTeam.getTeamColor().getChatColor() + player.getName() + " &7was killed by " + damagerTeam.getTeamColor().getChatColor() + damager.getName()));
            }
            arena.getDeadPlayers().add(player);
            player.setAllowFlight(true);
            player.setFlying(true);
            player.spigot().setCollidesWithEntities(false);
            player.getInventory().setArmorContents(null);
            player.getInventory().clear();
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
            player.setFireTicks(0);
            player.setHealth(20);
            Titles.sendTitle(player, 0, 40, 0, TextUtil.color("&c&lYOU DIED"), TextUtil.color("&7You will respawn at the start of the next round!"));
            if (arena.getAlivePlayers().size() == 0) {
                arena.getPlayingTask().getTask().cancel();
                arena.sendMessage("&cAll players died!");
                arena.setGameState(GameState.ROUND_OVER);
            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        if (GameUtil.getArenas().stream().anyMatch(arena -> arena.getArenaRegion().isInside(e.getLocation()))) {
            if (e.getEntity().getType().equals(EntityType.ARMOR_STAND)) return;
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
        Arena arena = GameUtil.getArenaByPlayer(player);
        if (arena.getGameState().equals(GameState.WAITING) || arena.getGameState().equals(GameState.STARTING)) {
            if (player.getLocation().getBlock().getType().equals(Material.LAVA) || player.getLocation().getBlock().getType().equals(Material.STATIONARY_LAVA)) {
                player.teleport(arena.getLobbyLocation());
                XSound.play(player, "ENTITY_ENDERMAN_TELEPORT");
            }
        } else if (arena.getGameState().equals(GameState.PLAYING)) {
            Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
            if (block.getType().equals(XMaterial.SLIME_BLOCK.parseMaterial()) && !arena.getDeadPlayers().contains(player)) {
                jumping.add(player);
                player.setVelocity(player.getLocation().getDirection().multiply(1.5).setY(1));
                XSound.play(player, "ENTITY_BAT_TAKEOFF");
            }
            for (PowerUp powerUp : arena.getPowerUps()) {
                double distance = player.getLocation().distance(powerUp.getLocation());
                if (distance <= 1 && powerUp.isActive()) {
                    powerUp.use(player);
                }
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if (GameUtil.isPlaying(e.getPlayer()) || GameUtil.isSpectating(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

}
