package me.cubecrafter.woolwars.listeners;

import com.cryptomorin.xseries.XMaterial;
import me.cubecrafter.woolwars.api.events.player.PlayerKillEvent;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.config.Abilities;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.database.PlayerData;
import me.cubecrafter.woolwars.menu.menus.KitsMenu;
import me.cubecrafter.woolwars.menu.menus.TeleporterMenu;
import me.cubecrafter.woolwars.powerup.PowerUp;
import me.cubecrafter.woolwars.team.Team;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import me.cubecrafter.woolwars.utils.VersionUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ArenaListener implements Listener {

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        if (ArenaUtil.getArenas().stream().anyMatch(arena -> arena.getArenaRegion().isInside(e.getLocation()))) {
            if (e.getEntityType() == EntityType.ARMOR_STAND) return;
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (ArenaUtil.getArenas().stream().anyMatch(arena -> arena.getArenaRegion().isInsideWithMarge(e.getLocation(), 10))) {
            e.setCancelled(true);
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
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL) && Configuration.DISABLE_FALL_DAMAGE.getAsBoolean()) {
            e.setCancelled(true);
            return;
        }
        if (arena.getGameState() != GameState.ACTIVE_ROUND || arena.getDeadPlayers().contains(player)) {
            if (e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
                player.setFireTicks(0);
            }
            e.setCancelled(true);
            return;
        }
        if (e instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent) e;
            if (damageByEntityEvent.getDamager() instanceof TNTPrimed) {
                TNTPrimed tnt = (TNTPrimed) damageByEntityEvent.getDamager();
                if (tnt.hasMetadata("woolwars")) {
                    player.setVelocity(player.getLocation().toVector().subtract(tnt.getLocation().toVector()).normalize().multiply(Abilities.KNOCKBACK_TNT.getAsSection().getDouble("knockback-power")));
                    e.setCancelled(true);
                    return;
                }
            } else if (damageByEntityEvent.getDamager() instanceof Player) {
                Player damager = (Player) damageByEntityEvent.getDamager();
                if (arena.getDeadPlayers().contains(damager) || arena.isTeammate(player, damager)) {
                    e.setCancelled(true);
                    return;
                }
            } else if (damageByEntityEvent.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) damageByEntityEvent.getDamager();
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
            Team playerTeam = arena.getTeamByPlayer(player);
            if (e instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
                if (event.getDamager() instanceof Player) {
                    Player damager = (Player) event.getDamager();
                    PlayerKillEvent killEvent = new PlayerKillEvent(damager, player, PlayerKillEvent.KillCause.PVP, arena);
                    Bukkit.getServer().getPluginManager().callEvent(killEvent);
                    Team damagerTeam = arena.getTeamByPlayer(damager);
                    TextUtil.sendMessage(arena.getPlayers(), Messages.KILL_MESSAGE.getAsString()
                            .replace("{player}", player.getName())
                            .replace("{player_team_color}", playerTeam.getTeamColor().getChatColor().toString())
                            .replace("{attacker}", damager.getName())
                            .replace("{attacker_team_color}", damagerTeam.getTeamColor().getChatColor().toString()));
                    ArenaUtil.playSound(damager, Configuration.SOUNDS_PLAYER_KILL.getAsString());
                    arena.getRoundTask().addKill(damager);
                } else if (event.getDamager() instanceof Projectile) {
                    PlayerKillEvent killEvent = new PlayerKillEvent(null, player, PlayerKillEvent.KillCause.PROJECTILE, arena);
                    Bukkit.getServer().getPluginManager().callEvent(killEvent);
                    Projectile projectile = (Projectile) event.getDamager();
                    if (projectile.getShooter() instanceof Player) {
                        Player damager = (Player) projectile.getShooter();
                        Team damagerTeam = arena.getTeamByPlayer(damager);
                        TextUtil.sendMessage(arena.getPlayers(), Messages.KILL_MESSAGE.getAsString()
                                .replace("{player}", player.getName())
                                .replace("{player_team_color}", playerTeam.getTeamColor().getChatColor().toString())
                                .replace("{attacker}", damager.getName())
                                .replace("{attacker_team_color}", damagerTeam.getTeamColor().getChatColor().toString()));
                        ArenaUtil.playSound(damager, Configuration.SOUNDS_PLAYER_KILL.getAsString());
                        arena.getRoundTask().addKill(damager);
                    }
                }
            } else if (e.getCause().equals(EntityDamageEvent.DamageCause.LAVA)) {
                PlayerKillEvent killEvent = new PlayerKillEvent(null, player, PlayerKillEvent.KillCause.LAVA, arena);
                Bukkit.getServer().getPluginManager().callEvent(killEvent);
                TextUtil.sendMessage(arena.getPlayers(), Messages.DEATH_BY_LAVA.getAsString()
                        .replace("{player}", player.getName())
                        .replace("{player_team_color}", playerTeam.getTeamColor().getChatColor().toString()));
            } else if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                PlayerKillEvent killEvent = new PlayerKillEvent(null, player, PlayerKillEvent.KillCause.FALL, arena);
                Bukkit.getServer().getPluginManager().callEvent(killEvent);
                TextUtil.sendMessage(arena.getPlayers(), Messages.DEATH_BY_FALL.getAsString()
                        .replace("{player}", player.getName())
                        .replace("{player_team_color}", playerTeam.getTeamColor().getChatColor().toString()));
            } else if (e.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                PlayerKillEvent killEvent = new PlayerKillEvent(null, player, PlayerKillEvent.KillCause.VOID, arena);
                Bukkit.getServer().getPluginManager().callEvent(killEvent);
                TextUtil.sendMessage(arena.getPlayers(), Messages.DEATH_BY_VOID.getAsString()
                        .replace("{player}", player.getName())
                        .replace("{player_team_color}", playerTeam.getTeamColor().getChatColor().toString()));
            } else {
                PlayerKillEvent killEvent = new PlayerKillEvent(null, player, PlayerKillEvent.KillCause.UNKNOWN, arena);
                Bukkit.getServer().getPluginManager().callEvent(killEvent);
                TextUtil.sendMessage(arena.getPlayers(), Messages.DEATH_GENERIC.getAsString()
                        .replace("{player}", player.getName())
                        .replace("{player_team_color}", playerTeam.getTeamColor().getChatColor().toString()));
            }
            handleDeath(player, arena);
        }
    }

    private void handleDeath(Player player, Arena arena) {
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
        for (Player alive : arena.getAlivePlayers()) {
            VersionUtil.hidePlayer(alive, player);
        }
        for (Player dead : arena.getDeadPlayers()) {
            VersionUtil.showPlayer(player, dead);
        }
        ItemStack teleporter = ItemBuilder.fromConfig(Configuration.TELEPORTER_ITEM.getAsSection()).setTag("teleport-item").build();
        player.getInventory().setItem(Configuration.TELEPORTER_ITEM.getAsSection().getInt("slot"), teleporter);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 0, false, false));
        if (arena.getAlivePlayers().isEmpty()) {
            TextUtil.sendMessage(arena.getPlayers(),  Messages.ALL_PLAYERS_DEAD.getAsString());
            arena.getRoundTask().cancel();
            arena.setGameState(GameState.ROUND_OVER);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
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
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        String tag = ItemBuilder.getTag(e.getItem());
        if (tag == null) return;
        switch (tag) {
            case "teleport-item":
                e.setCancelled(true);
                new TeleporterMenu(player, arena).openMenu();
                break;
            case "leave-item":
                e.setCancelled(true);
                arena.removePlayer(player, true);
                break;
            case "ability-item":
                e.setCancelled(true);
                ArenaUtil.getKitByPlayer(player).getAbility().use(player, arena);
                break;
            case "playagain-item":
                e.setCancelled(true);
                if (!player.hasPermission("woolwars.playagain")) {
                    TextUtil.sendMessage(player, Messages.NO_PERMISSION.getAsString());
                    break;
                }
                arena.removePlayer(player, false);
                if (!ArenaUtil.joinRandomArena(player, arena.getGroup())) {
                    ArenaUtil.teleportToLobby(player);
                }
                break;
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if (ArenaUtil.isPlaying(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent e) {
        if (ArenaUtil.isPlaying((Player) e.getEntity())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        if (arena.getGameState() != GameState.ACTIVE_ROUND) {
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
        if (arena.getGameState() == GameState.PRE_ROUND) {
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
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (arena == null) return;
        if (arena.getGameState() != GameState.ACTIVE_ROUND) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getFrom().getBlockX() == e.getTo().getBlockX() && e.getFrom().getBlockZ() == e.getTo().getBlockZ() && e.getFrom().getBlockY() == e.getTo().getBlockY()) return;
        Player player = e.getPlayer();
        if (!ArenaUtil.isPlaying(player)) return;
        Arena arena = ArenaUtil.getArenaByPlayer(player);
        if (!arena.getArenaRegion().isInside(e.getTo())) {
            if (arena.isDead(player)) {
                player.teleport(arena.getLobby());
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
                        player.teleport(arena.getLobby());
                        ArenaUtil.playSound(player, Configuration.SOUNDS_TELEPORT_TO_BASE.getAsString());
                        break;
                    case PRE_ROUND:
                        player.teleport(arena.getTeamByPlayer(player).getSpawnLocation());
                        ArenaUtil.playSound(player, Configuration.SOUNDS_TELEPORT_TO_BASE.getAsString());
                        break;
                }
            }
        }
        if (arena.getGameState() == GameState.WAITING || arena.getGameState() == GameState.STARTING) {
            if (player.getLocation().getBlock().getType().toString().contains("LAVA")) {
                player.teleport(arena.getLobby());
                ArenaUtil.playSound(player, Configuration.SOUNDS_TELEPORT_TO_BASE.getAsString());
            }
        } else if (arena.getGameState() == GameState.ACTIVE_ROUND && !arena.getDeadPlayers().contains(player)) {
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
    public void onWeatherChange(WeatherChangeEvent e) {
        e.setCancelled(true);
    }

}
