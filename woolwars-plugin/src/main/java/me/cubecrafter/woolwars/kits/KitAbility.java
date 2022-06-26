package me.cubecrafter.woolwars.kits;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.api.arena.Arena;
import me.cubecrafter.woolwars.api.arena.GameState;
import me.cubecrafter.woolwars.api.events.player.PlayerUseAbilityEvent;
import me.cubecrafter.woolwars.api.kits.Ability;
import me.cubecrafter.woolwars.api.kits.Kit;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class KitAbility implements Ability {

    private final static List<UUID> cooldown = new ArrayList<>();

    public static void removeCooldown(UUID uuid) {
        cooldown.remove(uuid);
    }

    private final String name;
    private final Kit kit;
    private final AbilityType abilityType;
    private final ItemStack item;
    private final int itemSlot;
    private final List<PotionEffect> effects = new ArrayList<>();

    public KitAbility(Kit kit, YamlConfiguration kitConfig) {
        this.kit = kit;
        name = kitConfig.getString("ability.displayname");
        abilityType = AbilityType.valueOf(kitConfig.getString("ability.type").toUpperCase());
        item = ItemBuilder.fromConfig(kitConfig.getConfigurationSection("ability.item")).setTag("ability-item").build();
        itemSlot = kitConfig.getInt("ability.item.slot");
        if (abilityType.equals(AbilityType.EFFECT)) {
            for (String effect : kitConfig.getStringList("ability.effects")) {
                effects.add(TextUtil.getEffect(effect));
            }
        }
    }

    @Override
    public void use(Player player, Arena arena) {
        PlayerUseAbilityEvent event = new PlayerUseAbilityEvent(player, this, arena);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        if (!arena.getGameState().equals(GameState.ACTIVE_ROUND)) {
            player.sendMessage(TextUtil.color("&cYou can't use your ability yet!"));
            return;
        }
        if (cooldown.contains(player.getUniqueId())) {
            player.sendMessage(TextUtil.color("&cYou have already used your ability!"));
            return;
        }
        switch (abilityType) {
            case EFFECT:
                for (PotionEffect effect : effects) {
                    player.addPotionEffect(effect);
                }
                cooldown.add(player.getUniqueId());
                break;
            case KNOCKBACK_TNT:
                TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(player.getLocation(), EntityType.PRIMED_TNT);
                tnt.setMetadata("woolwars", new FixedMetadataValue(WoolWars.getInstance(), "knockback-tnt"));
                tnt.setIsIncendiary(false);
                tnt.setFuseTicks(20);
                cooldown.add(player.getUniqueId());
                break;
            case STEP_BACK:
                player.setVelocity(player.getLocation().getDirection().multiply(-1.5));
                ArenaUtil.playSound(player, "ENTITY_ENDERMAN_TELEPORT");
                cooldown.add(player.getUniqueId());
                break;
            case GOLDEN_SHELL:
                player.getInventory().setHelmet(new ItemBuilder("GOLDEN_HELMET").build());
                player.getInventory().setChestplate(new ItemBuilder("GOLDEN_CHESTPLATE").build());
                player.getInventory().setLeggings(new ItemBuilder("GOLDEN_LEGGINGS").build());
                player.getInventory().setBoots(new ItemBuilder("GOLDEN_BOOTS").build());
                Bukkit.getScheduler().runTaskLater(WoolWars.getInstance(), () -> player.getInventory().setArmorContents(null), 100L);
                cooldown.add(player.getUniqueId());
                break;
            case HACK:
                if (arena.isCenterLocked()) {
                    TextUtil.sendMessage(player, "&cCenter is already locked!");
                    return;
                }
                arena.setCenterLocked(true);
                new BukkitRunnable() {
                    int timer = 5;
                    @Override
                    public void run() {
                        if (timer == 0) {
                            arena.setCenterLocked(false);
                            TextUtil.sendActionBar(arena.getPlayers(), "&e&lCENTER UNLOCKED!");
                            cancel();
                        } else {
                            TextUtil.sendActionBar(arena.getPlayers(), "&e&lCENTER UNLOCKS IN " + timer + " &e&lSECONDS!");
                            timer--;
                        }
                    }
                }.runTaskTimer(WoolWars.getInstance(), 0L, 20L);
                cooldown.add(player.getUniqueId());
                break;
        }
        TextUtil.sendMessage(player, "&aYou used your keystone ability: " + name);
    }

}
