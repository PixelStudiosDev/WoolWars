package me.cubecrafter.woolwars.game.tasks;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.game.arena.Arena;
import me.cubecrafter.woolwars.game.arena.GameState;
import me.cubecrafter.woolwars.game.kits.Kit;
import me.cubecrafter.woolwars.game.powerup.PowerUp;
import me.cubecrafter.woolwars.game.team.Team;
import me.cubecrafter.woolwars.menu.Menu;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class ArenaPreRoundTask extends ArenaTask {

    @Getter private final Set<Player> kitSelected = new HashSet<>();

    public ArenaPreRoundTask(Arena arena) {
        super(arena);
        arena.setRound(arena.getRound() + 1);
        arena.killEntities();
        arena.resetBlocks();
        arena.getTeams().forEach(Team::spawnBarrier);
        arena.respawnPlayers();
        ItemStack kitItem = new ItemBuilder("CHEST").setDisplayName("&eSelect Kit").setTag("kit-item").build();
        for (Player player : arena.getPlayers()) {
            player.getInventory().setArmorContents(null);
            player.getInventory().clear();
            player.getInventory().setItem(0, kitItem);
        }
    }


    @Override
    public void execute() {
        if (arena.getTimer() <= 3) {
            arena.sendTitle(20, "&c{seconds}".replace("{seconds}", String.valueOf(arena.getTimer())), "&7Get Ready");
            arena.playSound("ENTITY_CHICKEN_EGG");
        }
    }

    @Override
    public void onTimerEnd() {
        arena.getPlayers().forEach(player -> {
            if (player.getOpenInventory() != null && player.getOpenInventory().getTopInventory().getHolder() instanceof Menu) {
                player.closeInventory();
            }
        });
        if (arena.isLastRound()) {
            arena.sendTitle(40, "&a&lROUND START", "&bLast Round!");
        } else if (arena.isExtraRound()) {
            arena.sendTitle(40, "&a&lROUND START", "&bExtra Round!");
        } else {
            arena.sendTitle(40, "&a&lROUND START", "&bRound {round}".replace("{round}", String.valueOf(arena.getRound())));
        }
        Kit defaultKit = ArenaUtil.getKits().stream().filter(Kit::isDefaultKit).findAny().orElse(null);
        arena.getPlayers().stream().filter(player -> !kitSelected.contains(player)).forEach(player -> defaultKit.addToPlayer(player, arena.getTeamByPlayer(player)));
        kitSelected.clear();
        arena.playSound("BLOCK_ANVIL_LAND");
        arena.getTeams().forEach(Team::removeBarrier);
        arena.getPowerUps().forEach(PowerUp::spawn);
        for (Block block : arena.getBlocksRegion().getBlocks()) {
            if (block.hasMetadata("woolwars")) {
                block.removeMetadata("woolwars", WoolWars.getInstance());
            }
        }
        arena.setGameState(GameState.PLAYING);
    }

    @Override
    public int getTaskDuration() {
        return 10;
    }

}
