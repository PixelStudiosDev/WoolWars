package me.cubecrafter.woolwars.listeners;

import com.cryptomorin.xseries.messages.Titles;
import me.cubecrafter.woolwars.core.Arena;
import me.cubecrafter.woolwars.core.GameState;
import me.cubecrafter.woolwars.core.Team;
import me.cubecrafter.woolwars.utils.GameUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DeathListener implements Listener {

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
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
            Titles.sendTitle(player, 0, 40, 0, TextUtil.color("&c&lYOU DIED"), TextUtil.color("&7You will respawn at the start of the next round!"));
            if (e instanceof EntityDamageByEntityEvent) {
                Player damager = (Player) ((EntityDamageByEntityEvent) e).getDamager();
                Team playerTeam = arena.getTeamByPlayer(player);
                Team damagerTeam = arena.getTeamByPlayer(damager);
                arena.broadcast(TextUtil.color(playerTeam.getTeamColor().getChatColor() + player.getName() + " &7was killed by " + damagerTeam.getTeamColor().getChatColor() + damager.getName()));
            }
        }
    }

}
