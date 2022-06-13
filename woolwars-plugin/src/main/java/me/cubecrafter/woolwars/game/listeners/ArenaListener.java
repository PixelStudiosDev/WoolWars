package me.cubecrafter.woolwars.game.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.ActionBar;
import com.cryptomorin.xseries.messages.Titles;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.api.game.arena.GameState;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.database.PlayerData;
import me.cubecrafter.woolwars.database.StatisticType;
import me.cubecrafter.woolwars.game.arena.Arena;
import me.cubecrafter.woolwars.game.powerup.PowerUp;
import me.cubecrafter.woolwars.game.team.Team;
import me.cubecrafter.woolwars.menu.menus.KitsMenu;
import me.cubecrafter.woolwars.menu.menus.TeleportMenu;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ArenaListener implements Listener {

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent e) {
        Player player = (Player) e.getEntity();
        if (ArenaUtil.getArenaByPlayer(player) != null) {
            e.setCancelled(true);
        }
    }

    private final Set<Player> jumping = new HashSet<>();

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL) && jumping.contains(player)) {
            e.setCancelled(true);
            jumping.remove(player);
            return;
        }
        if (!arena.getGameState().equals(GameState.ACTIVE_ROUND) || arena.getDeadPlayers().contains(player)) {
            player.setFireTicks(0);
            player.setHealth(20);
            e.setCancelled(true);
            return;
        }
        if (e instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
            if (event.getDamager().getType().equals(EntityType.PRIMED_TNT)) {
                TNTPrimed tnt = (TNTPrimed) event.getDamager();
                if (tnt.hasMetadata("woolwars")) {
                    e.setCancelled(true);
                    player.setVelocity(player.getLocation().toVector().subtract(tnt.getLocation().toVector()).normalize().multiply(1.5));
                    return;
                }
            }
            if (event.getDamager() instanceof Player) {
                Player damager = (Player) event.getDamager();
                if (arena.getDeadPlayers().contains(damager) || arena.isTeammate(player, damager)) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
        if (((player.getHealth() - e.getFinalDamage()) <= 0)) {
            e.setCancelled(true);
            arena.addDeaths(player, 1);
            PlayerData data = ArenaUtil.getPlayerData(player);
            data.setStatistic(StatisticType.DEATHS, data.getStatistic(StatisticType.DEATHS) + 1);
            Team playerTeam = arena.getTeamByPlayer(player);
            if (e instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
                if (!(event.getDamager() instanceof Player)) return;
                Player damager = (Player) event.getDamager();
                Team damagerTeam = arena.getTeamByPlayer(damager);
                TextUtil.sendMessage(arena.getPlayers(), playerTeam.getTeamColor().getChatColor() + player.getName() + " &7was killed by " + damagerTeam.getTeamColor().getChatColor() + damager.getName());
                XSound.play(damager, "ENTITY_EXPERIENCE_ORB_PICKUP");
                arena.getPlayingTask().addKill(damager);
            } else if (e.getCause().equals(EntityDamageEvent.DamageCause.LAVA)) {
                TextUtil.sendMessage(arena.getPlayers(), playerTeam.getTeamColor().getChatColor() + player.getName() + " &7burned to death ");
            } else if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                TextUtil.sendMessage(arena.getPlayers(), playerTeam.getTeamColor().getChatColor() + player.getName() + " &7fell from a high place ");
            }
            arena.getDeadPlayers().add(player);
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.setFlying(true);
            player.getInventory().setArmorContents(null);
            player.getInventory().clear();
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
            player.setFireTicks(0);
            player.setHealth(20);
            Titles.sendTitle(player, 0, 40, 0, TextUtil.color("&c&lYOU DIED"), TextUtil.color("&7You will respawn at the start of the next round!"));
            ActionBar.sendActionBarWhile(WoolWars.getInstance(), player, TextUtil.color("&eYou will respawn next round!"), () -> arena.isDead(player));
            ArenaUtil.hideDeadPlayer(player, arena);
            ItemStack teleporter = new ItemBuilder("COMPASS").setDisplayName("&cTeleporter").setTag("teleport-item").build();
            player.getInventory().setItem(0, teleporter);
            if (arena.getAlivePlayers().size() == 0) {
                arena.getPlayingTask().cancelTask();
                TextUtil.sendMessage(arena.getPlayers(), "&cAll players died!");
                arena.getPlayingTask().getRotatePowerUpsTask().cancel();
                arena.setGameState(GameState.ROUND_OVER);
            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        if (ArenaUtil.getArenas().stream().anyMatch(arena -> arena.getArenaRegion().isInside(e.getLocation()))) {
            if (e.getEntity().getType().equals(EntityType.ARMOR_STAND)) return;
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            for (String material : Configuration.DISABLE_INTERACTION_BLOCKS.getAsStringList()) {
                if (e.getClickedBlock().getType().equals(XMaterial.matchXMaterial(material).get().parseMaterial())) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
        if (e.getItem() == null) return;
        if (e.getItem().getType().toString().contains("POTION")) {
            if (!arena.getGameState().equals(GameState.ACTIVE_ROUND)) {
                e.setCancelled(true);
                return;
            }
        }
        if (!e.getAction().equals(Action.RIGHT_CLICK_AIR) && !e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (ItemBuilder.hasTag(e.getItem(), "leave-item")) {
            arena.removePlayer(player, true);
            return;
        }
        if (ItemBuilder.hasTag(e.getItem(), "teleport-item")) {
            new TeleportMenu(player).openMenu();
            return;
        }
        if (ItemBuilder.hasTag(e.getItem(), "playagain-item")) {
            if (ArenaUtil.joinRandom(player)) {
                arena.removePlayer(player, false);
            }
            return;
        }
        if (ItemBuilder.hasTag(e.getItem(), "ability-item")) {
            ArenaUtil.getKitByPlayer(player).getAbility().use(player);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (!ArenaUtil.isPlaying(player)) return;
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena.getGameState().equals(GameState.WAITING) || arena.getGameState().equals(GameState.STARTING)) {
            if (player.getLocation().getBlock().getType().toString().contains("LAVA")) {
                player.teleport(arena.getLobbyLocation());
                XSound.play(player, "ENTITY_ENDERMAN_TELEPORT");
            }
        } else if (arena.getGameState().equals(GameState.ACTIVE_ROUND) && !arena.getDeadPlayers().contains(player)) {
            Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
            if (block.getType().equals(XMaterial.SLIME_BLOCK.parseMaterial())) {
                jumping.add(player);
                player.setVelocity(player.getLocation().getDirection().normalize().multiply(0.5).setY(1));
                ArenaUtil.playSound(player, "ENTITY_BAT_TAKEOFF");
            }
            for (PowerUp powerUp : arena.getPowerUps().stream().filter(PowerUp::isActive).collect(Collectors.toList())) {
                double distance = player.getLocation().distance(powerUp.getLocation());
                if (distance <= 1) {
                    powerUp.use(player);
                    PlayerData data = ArenaUtil.getPlayerData(player);
                    data.setStatistic(StatisticType.POWERUPS_COLLECTED, data.getStatistic(StatisticType.POWERUPS_COLLECTED) + 1);
                }
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if (ArenaUtil.getArenaByPlayer(e.getPlayer()) != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent e) {
        if (ArenaUtil.getArenas().stream().anyMatch(arena -> arena.getArenaRegion().isInsideWithMarge(e.getLocation(), 10))) {
            e.blockList().clear();
        }
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        if (!arena.getGameState().equals(GameState.ACTIVE_ROUND)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpectate(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        if (!arena.getDeadPlayers().contains(player)) return;
        if (!(e.getRightClicked() instanceof Player)) return;
        Player clicked = (Player) e.getRightClicked();
        Arena other = ArenaUtil.getArenaByPlayer(clicked);
        if (!arena.equals(other)) return;
        player.setGameMode(GameMode.SPECTATOR);
        player.setSpectatorTarget(clicked);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        if (arena.getGameState().equals(GameState.PRE_ROUND)) {
            new KitsMenu(player).openMenu();
        }
        if (!arena.getDeadPlayers().contains(player)) return;
        if (player.getSpectatorTarget() == null) return;
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    @EventHandler
    public void onPotionDrink(PlayerItemConsumeEvent e) {
        Player player = e.getPlayer();
        if (!e.getItem().getType().toString().contains("POTION")) return;
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        if (arena.getGameState().equals(GameState.ACTIVE_ROUND)) return;
        e.setCancelled(true);
    }

}
