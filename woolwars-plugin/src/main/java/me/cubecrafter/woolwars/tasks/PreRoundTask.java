package me.cubecrafter.woolwars.tasks;

import com.cryptomorin.xseries.messages.ActionBar;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.api.database.PlayerData;
import me.cubecrafter.woolwars.arena.GameArena;
import me.cubecrafter.woolwars.api.arena.GameState;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.kits.Ability;
import me.cubecrafter.woolwars.kits.Kit;
import me.cubecrafter.woolwars.powerup.PowerUp;
import me.cubecrafter.woolwars.team.GameTeam;
import me.cubecrafter.woolwars.menu.Menu;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PreRoundTask extends ArenaTask {

    public PreRoundTask(GameArena arena) {
        super(arena);
    }


    @Override
    public void execute() {
        if (arena.getTimer() <= 3) {
            TextUtil.sendTitle(arena.getPlayers(), 1, "&c{seconds}".replace("{seconds}", String.valueOf(arena.getTimer())), "&7Get Ready");
            ArenaUtil.playSound(arena.getPlayers(), Configuration.SOUNDS_COUNTDOWN.getAsString());
        }
    }

    @Override
    public void onEnd() {
        arena.getPlayers().forEach(player -> {
            if (player.getOpenInventory() != null && player.getOpenInventory().getTopInventory().getHolder() instanceof Menu) {
                player.closeInventory();
            }
        });
        TextUtil.sendTitle(arena.getPlayers(), 1, "&a&lROUND START", "&bRound {round}".replace("{round}", String.valueOf(arena.getRound())));
        ArenaUtil.playSound(arena.getPlayers(), Configuration.SOUNDS_ROUND_START.getAsString());
        arena.getTeams().forEach(GameTeam::removeBarrier);
        arena.getPowerUps().forEach(PowerUp::spawn);
        for (Block block : arena.getWoolRegion().getBlocks()) {
            if (block.hasMetadata("woolwars")) {
                block.removeMetadata("woolwars", WoolWars.getInstance());
            }
        }
        arena.setGameState(GameState.ACTIVE_ROUND);
    }

    @Override
    public int getDuration() {
        return Configuration.PRE_ROUND_COUNTDOWN.getAsInt();
    }

    @Override
    public void onStart() {
        arena.setRound(arena.getRound() + 1);
        arena.killEntities();
        arena.resetBlocks();
        arena.getTeams().forEach(GameTeam::spawnBarrier);
        arena.getPlayers().forEach(player -> Ability.removeCooldown(player.getUniqueId()));
        for (Player player : arena.getPlayers()) {
            player.setGameMode(GameMode.SURVIVAL);
            player.setFlying(false);
            player.setAllowFlight(false);
            player.setHealth(20);
            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        }
        arena.getDeadPlayers().clear();
        arena.getTeams().forEach(GameTeam::teleportToSpawn);
        for (Player player : arena.getPlayers()) {
            arena.getPlayers().forEach(player::showPlayer);
        }
        for (Player player : arena.getPlayers()) {
            player.getInventory().setArmorContents(null);
            player.getInventory().clear();
            PlayerData data = WoolWars.getInstance().getPlayerDataManager().getPlayerData(player);
            String selected = data.getSelectedKit();
            Kit kit;
            if (selected == null) {
                kit = ArenaUtil.getKits().stream().filter(Kit::isDefaultKit).findAny().orElse(null);
                data.setSelectedKit(kit.getId());
            } else {
                kit = ArenaUtil.getKit(selected);
            }
            kit.addToPlayer(player, arena.getTeamByPlayer(player));
            ActionBar.sendActionBarWhile(WoolWars.getInstance(), player, TextUtil.color("&eShift to select a kit!"), () -> arena.getGameState().equals(GameState.PRE_ROUND));
        }
    }

}
