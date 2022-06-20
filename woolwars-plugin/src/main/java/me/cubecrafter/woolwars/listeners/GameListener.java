package me.cubecrafter.woolwars.listeners;

import com.cryptomorin.xseries.XMaterial;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.api.arena.GameState;
import me.cubecrafter.woolwars.api.database.PlayerData;
import me.cubecrafter.woolwars.api.nms.NMS;
import me.cubecrafter.woolwars.arena.GameArena;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.menu.menus.KitsMenu;
import me.cubecrafter.woolwars.menu.menus.TeleporterMenu;
import me.cubecrafter.woolwars.powerup.PowerUp;
import me.cubecrafter.woolwars.team.GameTeam;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GameListener implements Listener {

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent e) {
        Player player = (Player) e.getEntity();
        if (ArenaUtil.isPlaying(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        GameArena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL) && Configuration.DISABLE_FALL_DAMAGE.getAsBoolean()) {
            e.setCancelled(true);
            return;
        }
        if (!arena.getGameState().equals(GameState.ACTIVE_ROUND) || arena.getDeadPlayers().contains(player)) {
            player.setFireTicks(0);
            e.setCancelled(true);
            return;
        }
        if (e instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
            if (event.getDamager() instanceof TNTPrimed) {
                TNTPrimed tnt = (TNTPrimed) event.getDamager();
                if (tnt.hasMetadata("woolwars")) {
                    player.setVelocity(player.getLocation().toVector().subtract(tnt.getLocation().toVector()).normalize().multiply(1.5));
                    e.setCancelled(true);
                    return;
                }
            } else if (event.getDamager() instanceof Player) {
                Player damager = (Player) event.getDamager();
                if (arena.getDeadPlayers().contains(damager) || arena.isTeammate(player, damager)) {
                    e.setCancelled(true);
                    return;
                }
            } else if (event.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) event.getDamager();
                projectile.remove();
                if (projectile.getShooter() instanceof Player) {
                    Player damager = (Player) projectile.getShooter();
                    if (arena.isTeammate(player, damager)) {
                        e.setCancelled(true);
                        return;
                    }
                }
            }
        }
        if (((player.getHealth() - e.getFinalDamage()) <= 0)) {
            e.setCancelled(true);
            GameTeam playerTeam = arena.getTeamByPlayer(player);
            if (e instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
                if (event.getDamager() instanceof Player) {
                    Player damager = (Player) event.getDamager();
                    GameTeam damagerTeam = arena.getTeamByPlayer(damager);
                    TextUtil.sendMessage(arena.getPlayers(), Messages.KILL_MESSAGE.getAsString()
                            .replace("{player}", player.getName())
                            .replace("{player_team_color}", playerTeam.getTeamColor().getChatColor().toString())
                            .replace("{attacker}", damager.getName())
                            .replace("{attacker_team_color}", damagerTeam.getTeamColor().getChatColor().toString()));
                    ArenaUtil.playSound(damager, Configuration.SOUNDS_PLAYER_KILL.getAsString());
                    arena.getRoundTask().addKill(damager);
                } else if (event.getDamager() instanceof Projectile) {
                    Projectile projectile = (Projectile) event.getDamager();
                    if (projectile.getShooter() instanceof Player) {
                        Player damager = (Player) projectile.getShooter();
                        GameTeam damagerTeam = arena.getTeamByPlayer(damager);
                        TextUtil.sendMessage(arena.getPlayers(), Messages.KILL_MESSAGE.getAsString()
                                .replace("{player}", player.getDisplayName())
                                .replace("{player_team_color}", playerTeam.getTeamColor().getChatColor().toString())
                                .replace("{attacker}", damager.getDisplayName())
                                .replace("{attacker_team_color}", damagerTeam.getTeamColor().getChatColor().toString()));
                        ArenaUtil.playSound(damager, Configuration.SOUNDS_PLAYER_KILL.getAsString());
                        arena.getRoundTask().addKill(damager);
                    }
                }
            } else if (e.getCause().equals(EntityDamageEvent.DamageCause.LAVA)) {
                TextUtil.sendMessage(arena.getPlayers(), Messages.DEATH_BY_LAVA.getAsString()
                        .replace("{player}", player.getDisplayName())
                        .replace("{player_team_color}", playerTeam.getTeamColor().getChatColor().toString()));
            } else if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                TextUtil.sendMessage(arena.getPlayers(), Messages.DEATH_BY_FALL.getAsString()
                        .replace("{player}", player.getDisplayName())
                        .replace("{player_team_color}", playerTeam.getTeamColor().getChatColor().toString()));
            } else if (e.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                TextUtil.sendMessage(arena.getPlayers(), Messages.DEATH_BY_VOID.getAsString()
                        .replace("{player}", player.getDisplayName())
                        .replace("{player_team_color}", playerTeam.getTeamColor().getChatColor().toString()));
            } else {
                TextUtil.sendMessage(arena.getPlayers(), Messages.DEATH_GENERIC.getAsString()
                        .replace("{player}", player.getDisplayName())
                        .replace("{player_team_color}", playerTeam.getTeamColor().getChatColor().toString()));
            }
            handleDeath(player, arena);
        }
    }

    private void handleDeath(Player player, GameArena arena) {
        ArenaUtil.playSound(player, Configuration.SOUNDS_PLAYER_DEATH.getAsString());
        arena.addDeaths(player, 1);
        PlayerData data = ArenaUtil.getPlayerData(player);
        data.setDeaths(data.getDeaths() + 1);
        arena.getDeadPlayers().add(player);
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.getInventory().setArmorContents(null);
        player.getInventory().clear();
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
        player.setFireTicks(0);
        player.setHealth(20);
        TextUtil.sendTitle(player, 2,  Messages.DEATH_TITLE.getAsString(), Messages.DEATH_SUBTITLE.getAsString());
        NMS nms = WoolWars.getInstance().getNms();
        for (Player alive : arena.getAlivePlayers()) {
            nms.hidePlayer(alive, player);
        }
        for (Player dead : arena.getDeadPlayers()) {
            nms.showPlayer(player, dead);
        }
        ItemStack teleporter = ItemBuilder.fromConfig(Configuration.TELEPORTER_ITEM.getAsConfigSection()).setTag("teleport-item").build();
        player.getInventory().setItem(Configuration.TELEPORTER_ITEM.getAsConfigSection().getInt("slot"), teleporter);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 0, false, false));
        if (arena.getAlivePlayers().size() == 0) {
            TextUtil.sendMessage(arena.getPlayers(),  Messages.ALL_PLAYERS_DEAD.getAsString());
            arena.getRoundTask().getRotatePowerUpsTask().cancel();
            arena.getRoundTask().cancel();
            arena.setGameState(GameState.ROUND_OVER);
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
        GameArena arena = ArenaUtil.getArenaByPlayer(player);
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
        String tag = ItemBuilder.getTag(e.getItem());
        if (tag == null) return;
        switch (tag) {
            case "teleport-item":
                new TeleporterMenu(player, arena).openMenu();
                break;
            case "leave-item":
                arena.removePlayer(player, true);
                break;
            case "ability-item":
                ArenaUtil.getKitByPlayer(player).getAbility().use(player);
                break;
            case "playagain-item":
                if (!player.hasPermission("woolwars.playagain")) {
                    TextUtil.sendMessage(player, "&cYou don't have the permission to play again!");
                    break;
                }
                if (ArenaUtil.joinRandomFromGroup(player, arena.getGroup())) {
                    arena.removePlayer(player, false);
                }
                break;
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getFrom().getBlockX() == e.getTo().getBlockX() && e.getFrom().getBlockZ() == e.getTo().getBlockZ() && e.getFrom().getBlockY() == e.getTo().getBlockY()) return;
        Player player = e.getPlayer();
        if (!ArenaUtil.isPlaying(player)) return;
        GameArena arena = ArenaUtil.getArenaByPlayer(player);
        if (!arena.getArenaRegion().isInside(e.getTo())) {
            if (arena.isDead(player)) {
                player.teleport(arena.getLobbyLocation());
                ArenaUtil.playSound(player, Configuration.SOUNDS_TELEPORT_TO_BASE.getAsString());
            } else {
                switch (arena.getGameState()) {
                    case ACTIVE_ROUND:
                        handleDeath(player, arena);
                        break;
                    case WAITING:
                    case STARTING:
                    case ROUND_OVER:
                    case GAME_ENDED:
                        player.teleport(arena.getLobbyLocation());
                        ArenaUtil.playSound(player, Configuration.SOUNDS_TELEPORT_TO_BASE.getAsString());
                        break;
                    case PRE_ROUND:
                        player.teleport(arena.getTeamByPlayer(player).getSpawnLocation());
                        ArenaUtil.playSound(player, Configuration.SOUNDS_TELEPORT_TO_BASE.getAsString());
                        break;
                }
            }
        }
        if (arena.getGameState().equals(GameState.WAITING) || arena.getGameState().equals(GameState.STARTING)) {
            if (player.getLocation().getBlock().getType().toString().contains("LAVA")) {
                player.teleport(arena.getLobbyLocation());
                ArenaUtil.playSound(player, Configuration.SOUNDS_TELEPORT_TO_BASE.getAsString());
            }
        } else if (arena.getGameState().equals(GameState.ACTIVE_ROUND) && !arena.getDeadPlayers().contains(player)) {
            Block top = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
            Block bottom = top.getRelative(BlockFace.DOWN);
            if (top.getType().equals(XMaterial.matchXMaterial(Configuration.JUMP_PADS_TOP_BLOCK.getAsString()).get().parseMaterial())
                    && bottom.getType().equals(XMaterial.matchXMaterial(Configuration.JUMP_PADS_BOTTOM_BLOCK.getAsString()).get().parseMaterial())) {
                player.setVelocity(player.getLocation().getDirection().normalize().multiply(Configuration.JUMP_PADS_HORIZONTAL_POWER.getAsDouble()).setY(Configuration.JUMP_PADS_VERTICAL_POWER.getAsDouble()));
                ArenaUtil.playSound(player, Configuration.SOUNDS_JUMP_PAD.getAsString());
            }
            for (PowerUp powerUp : arena.getPowerUps()) {
                if (!powerUp.isActive()) continue;
                double distance = player.getLocation().distance(powerUp.getLocation());
                if (distance <= 1) {
                    powerUp.use(player);
                    PlayerData data = ArenaUtil.getPlayerData(player);
                    data.setPowerUpsCollected(data.getPowerUpsCollected() + 1);
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
    public void onProjectileHit(ProjectileHitEvent e) {
        Projectile projectile = e.getEntity();
        if (projectile.getShooter() instanceof Player) {
            Player player = (Player) projectile.getShooter();
            if (!ArenaUtil.isPlaying(player)) return;
            projectile.remove();
        }
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        GameArena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        if (!arena.getGameState().equals(GameState.ACTIVE_ROUND)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpectate(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        GameArena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        if (!arena.getDeadPlayers().contains(player)) return;
        if (!(e.getRightClicked() instanceof Player)) return;
        Player clicked = (Player) e.getRightClicked();
        GameArena other = ArenaUtil.getArenaByPlayer(clicked);
        if (!arena.equals(other)) return;
        player.setGameMode(GameMode.SPECTATOR);
        player.setSpectatorTarget(clicked);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        GameArena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        if (arena.getGameState().equals(GameState.PRE_ROUND)) {
            new KitsMenu(player, arena).openMenu();
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
        GameArena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        if (arena.getGameState().equals(GameState.ACTIVE_ROUND)) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (!ArenaUtil.isPlaying(player)) return;
        GameArena arena = ArenaUtil.getArenaByPlayer(player);
        if (!arena.getGameState().equals(GameState.ACTIVE_ROUND) || arena.getDeadPlayers().contains(player)) {
            e.setCancelled(true);
            TextUtil.sendMessage(player, Messages.CANT_BREAK_BLOCK.getAsString());
        } else if (arena.getWoolRegion().isInside(e.getBlock().getLocation())) {
            if (arena.isCenterLocked()) {
                TextUtil.sendMessage(player, "&cCenter is locked!");
                e.setCancelled(true);
                return;
            }
            if (e.getBlock().hasMetadata("woolwars")) {
                if (!e.getBlock().getMetadata("woolwars").isEmpty()) {
                    String teamName = e.getBlock().getMetadata("woolwars").get(0).asString();
                    GameTeam team = arena.getTeamByName(teamName);
                    arena.getRoundTask().removePlacedWool(team);
                    e.getBlock().removeMetadata("woolwars", WoolWars.getInstance());
                }
            }
            e.getBlock().setType(Material.AIR);
            arena.getRoundTask().addBrokenBlock(player);
        } else if (!arena.getArenaPlacedBlocks().contains(e.getBlock())) {
            e.setCancelled(true);
            TextUtil.sendMessage(player, Messages.CANT_BREAK_BLOCK.getAsString());
        } else {
            arena.getArenaPlacedBlocks().remove(e.getBlock());
            e.getBlock().setType(Material.AIR);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        GameArena arena = ArenaUtil.getArenaByPlayer(player);
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
            if (arena.getWoolRegion().isInside(e.getBlock().getLocation())) {
                if (arena.isCenterLocked()) {
                    TextUtil.sendMessage(player, "&cCenter is locked!");
                    e.setCancelled(true);
                    return;
                }
                if (e.getBlock().getType().toString().endsWith("WOOL")) {
                    GameTeam team = arena.getTeamByPlayer(player);
                    e.getBlock().setMetadata("woolwars", new FixedMetadataValue(WoolWars.getInstance(), team.getName()));
                    arena.getRoundTask().addPlacedWool(team);
                    arena.getRoundTask().addPlacedWool(player);
                    arena.getRoundTask().checkWinners();
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
            TextUtil.sendMessage(player, Messages.CANT_PLACE_BLOCK.getAsString());
        } else {
            e.setCancelled(true);
            TextUtil.sendMessage(player, Messages.CANT_PLACE_BLOCK.getAsString());
        }
    }

}
