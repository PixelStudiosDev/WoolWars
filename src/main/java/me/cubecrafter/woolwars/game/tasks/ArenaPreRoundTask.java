package me.cubecrafter.woolwars.game.tasks;

import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.game.arena.Arena;
import me.cubecrafter.woolwars.game.GameState;
import me.cubecrafter.woolwars.game.powerup.PowerUp;
import me.cubecrafter.woolwars.game.team.Team;
import me.cubecrafter.woolwars.kits.Kit;
import me.cubecrafter.woolwars.utils.GameUtil;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class ArenaPreRoundTask extends ArenaTask {

    @Getter private final Set<Player> kitSelected = new HashSet<>();

    public ArenaPreRoundTask(Arena arena) {
        super(arena);
        arena.setTimer(10);
        arena.setRound(arena.getRound() + 1);
        arena.killEntities();
        arena.resetBlocks();
        arena.respawnPlayers();
        ItemStack kitItem = new ItemBuilder("CHEST").setDisplayName("&eSelect Kit").setNBT("woolwars", "kit-item").build();
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
        arena.getPlayers().forEach(HumanEntity::closeInventory);
        if (arena.isLastRound()) {
            arena.sendTitle(40, "&a&lROUND START", "&bLast Round!");
        } else if (arena.isExtraRound()) {
            arena.sendTitle(40, "&a&lROUND START", "&bExtra Round!");
        } else {
            arena.sendTitle(40, "&a&lROUND START", "&bRound {round}".replace("{round}", String.valueOf(arena.getRound())));
        }
        Kit defaultKit = GameUtil.getKits().stream().filter(Kit::isDefaultKit).findAny().orElse(null);
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

}
