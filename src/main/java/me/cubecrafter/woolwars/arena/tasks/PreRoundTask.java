package me.cubecrafter.woolwars.arena.tasks;

import com.cryptomorin.xseries.messages.ActionBar;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.database.PlayerData;
import me.cubecrafter.woolwars.api.events.arena.RoundStartEvent;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.kits.Kit;
import me.cubecrafter.woolwars.menu.Menu;
import me.cubecrafter.woolwars.powerup.PowerUp;
import me.cubecrafter.woolwars.team.Team;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import me.cubecrafter.woolwars.utils.VersionUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PreRoundTask extends ArenaTask {

    public PreRoundTask(Arena arena) {
        super(arena, Configuration.PRE_ROUND_DURATION.getAsInt());
    }

    @Override
    public void onStart() {
        arena.setRound(arena.getRound() + 1);
        TextUtil.sendTitle(arena.getPlayers(), 2, Messages.PRE_ROUND_TITLE.getAsString(), Messages.PRE_ROUND_SUBTITLE.getAsString());
        arena.killEntities();
        arena.removePlacedBlocks();
        arena.fillCenter();
        arena.getTeams().forEach(Team::spawnBarrier);
        arena.getPlayers().forEach(player -> WoolWars.getInstance().getKitManager().removeCooldown(player));
        for (Player player : arena.getPlayers()) {
            player.setGameMode(GameMode.SURVIVAL);
            player.setFlying(false);
            player.setAllowFlight(false);
            player.setHealth(20);
            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        }
        arena.getDeadPlayers().clear();
        arena.getTeams().forEach(Team::teleportToSpawn);
        for (Player player : arena.getPlayers()) {
            arena.getPlayers().forEach(other -> VersionUtil.showPlayer(player, other));
        }
        for (Player player : arena.getPlayers()) {
            player.getInventory().setArmorContents(null);
            player.getInventory().clear();
            PlayerData data = ArenaUtil.getPlayerData(player);
            String selected = data.getSelectedKit();
            Kit kit;
            if (selected == null) {
                kit = ArenaUtil.getKits().get(0);
                data.setSelectedKit(kit.getId());
            } else {
                kit = ArenaUtil.getKit(selected);
            }
            kit.addToPlayer(player, arena.getTeamByPlayer(player));
            ActionBar.sendActionBarWhile(WoolWars.getInstance(), player, TextUtil.color(Messages.SHIFT_TO_SELECT_KIT.getAsString()), () -> arena.getGameState().equals(GameState.PRE_ROUND));
        }
    }

    @Override
    public void execute() {
        if (arena.getTimer() <= 3) {
            String[] numbers = new String[] {"&c❶", "&6❷", "&a❸"};
            TextUtil.sendTitle(arena.getPlayers(), 1, Messages.ROUND_START_COUNTDOWN_TITLE.getAsString().replace("{seconds}", numbers[arena.getTimer() - 1]), Messages.ROUND_START_COUNTDOWN_SUBTITLE.getAsString());
            ArenaUtil.playSound(arena.getPlayers(), Configuration.SOUNDS_COUNTDOWN.getAsString());
        }
    }

    @Override
    public void onEnd() {
        arena.getPlayers().forEach(HumanEntity::closeInventory);
        TextUtil.sendTitle(arena.getPlayers(), 1, Messages.ROUND_START_TITLE.getAsString(), Messages.ROUND_START_SUBTITLE.getAsString().replace("{round}", String.valueOf(arena.getRound())));
        new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                ArenaUtil.playSound(arena.getPlayers(), Configuration.SOUNDS_ROUND_START.getAsString());
                i++;
                if (i == 3) cancel();
            }
        }.runTaskTimer(WoolWars.getInstance(), 0, 3L);
        arena.getTeams().forEach(Team::removeBarrier);
        arena.getPowerUps().forEach(PowerUp::spawn);
        RoundStartEvent event = new RoundStartEvent(arena, arena.getRound());
        Bukkit.getPluginManager().callEvent(event);
        arena.setGameState(GameState.ACTIVE_ROUND);
    }

}
