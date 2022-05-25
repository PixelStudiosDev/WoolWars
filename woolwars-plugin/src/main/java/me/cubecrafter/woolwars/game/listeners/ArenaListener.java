package me.cubecrafter.woolwars.game.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.Titles;
import me.cubecrafter.woolwars.config.ConfigPath;
import me.cubecrafter.woolwars.database.PlayerData;
import me.cubecrafter.woolwars.database.StatisticType;
import me.cubecrafter.woolwars.game.arena.Arena;
import me.cubecrafter.woolwars.game.arena.ArenaState;
import me.cubecrafter.woolwars.game.arena.GamePhase;
import me.cubecrafter.woolwars.game.powerup.PowerUp;
import me.cubecrafter.woolwars.game.team.Team;
import me.cubecrafter.woolwars.menu.menus.KitsMenu;
import me.cubecrafter.woolwars.menu.menus.TeleportMenu;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
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
        if (!ArenaUtil.isPlaying(player)) return;
        if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL) && jumping.contains(player)) {
            e.setCancelled(true);
            jumping.remove(player);
            return;
        }
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (!arena.getGamePhase().equals(GamePhase.ACTIVE_ROUND) || arena.getDeadPlayers().contains(player)) {
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
            arena.addDeaths(player, 1);
            PlayerData data = ArenaUtil.getPlayerData(player);
            data.setStatistic(StatisticType.DEATHS, data.getStatistic(StatisticType.DEATHS) + 1);
            Team playerTeam = arena.getTeamByPlayer(player);
            if (e instanceof EntityDamageByEntityEvent) {
                if (!(((EntityDamageByEntityEvent) e).getDamager() instanceof Player)) return;
                Player damager = (Player) ((EntityDamageByEntityEvent) e).getDamager();
                Team damagerTeam = arena.getTeamByPlayer(damager);
                arena.sendMessage(playerTeam.getTeamColor().getChatColor() + player.getName() + " &7was killed by " + damagerTeam.getTeamColor().getChatColor() + damager.getName());
                XSound.play(damager, "ENTITY_EXPERIENCE_ORB_PICKUP");
                arena.getPlayingTask().addKill(damager);
            } else if (e.getCause().equals(EntityDamageEvent.DamageCause.LAVA)) {
                arena.sendMessage(playerTeam.getTeamColor().getChatColor() + player.getName() + " &7burned to death ");
            } else if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                arena.sendMessage(playerTeam.getTeamColor().getChatColor() + player.getName() + " &7fell from a high place ");
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
            ArenaUtil.hideDeadPlayer(player, arena);
            ItemStack teleporter = new ItemBuilder("COMPASS").setDisplayName("&cTeleporter").setTag("teleport-item").build();
            player.getInventory().setItem(0, teleporter);
            if (arena.getAlivePlayers().size() == 0) {
                arena.getPlayingTask().cancelTask();
                arena.sendMessage("&cAll players died!");
                arena.getPlayingTask().getRotatePowerUpsTask().cancel();
                arena.setGamePhase(GamePhase.ROUND_OVER);
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
            for (String material : ConfigPath.DISABLE_INTERACTION_BLOCKS.getAsStringList()) {
                if (e.getClickedBlock().getType().equals(XMaterial.matchXMaterial(material).get().parseMaterial())) {
                    e.setCancelled(true);
                }
            }
        }
        if (e.getItem() == null) return;
        if (ItemBuilder.hasTag(e.getItem(), "leave-item")) {
            arena.removePlayer(player, true);
        }
        if (ItemBuilder.hasTag(e.getItem(), "teleport-item")) {
            new TeleportMenu(player).openMenu();
        }
        if (ItemBuilder.hasTag(e.getItem(), "playagain-item")) {
            arena.removePlayer(player, false);
            ArenaUtil.joinRandom(player);
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
        if (arena.getArenaState().equals(ArenaState.WAITING) || arena.getArenaState().equals(ArenaState.STARTING)) {
            if (player.getLocation().getBlock().getType().equals(Material.LAVA) || player.getLocation().getBlock().getType().equals(Material.STATIONARY_LAVA)) {
                player.teleport(arena.getLobbyLocation());
                XSound.play(player, "ENTITY_ENDERMAN_TELEPORT");
            }
        } else if (arena.getGamePhase().equals(GamePhase.ACTIVE_ROUND) && !arena.getDeadPlayers().contains(player)) {
            Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
            if (block.getType().equals(XMaterial.SLIME_BLOCK.parseMaterial())) {
                jumping.add(player);
                player.setVelocity(player.getLocation().getDirection().setY(1));
                XSound.play(player, "ENTITY_BAT_TAKEOFF");
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
        if (ArenaUtil.getArenas().stream().anyMatch(arena -> arena.getArenaRegion().isInside(e.getLocation()))) {
            e.blockList().clear();
        }
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        if (!arena.getGamePhase().equals(GamePhase.ACTIVE_ROUND)) {
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
        if (arena.getGamePhase().equals(GamePhase.PRE_ROUND)) {
            new KitsMenu(player).openMenu();
        }
        if (!arena.getDeadPlayers().contains(player)) return;
        if (player.getSpectatorTarget() == null) return;
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
    }

}
