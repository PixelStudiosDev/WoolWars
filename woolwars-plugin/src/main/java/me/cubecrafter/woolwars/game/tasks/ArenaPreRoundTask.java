package me.cubecrafter.woolwars.game.tasks;

import com.cryptomorin.xseries.messages.ActionBar;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.database.PlayerData;
import me.cubecrafter.woolwars.game.arena.Arena;
import me.cubecrafter.woolwars.game.arena.GamePhase;
import me.cubecrafter.woolwars.game.kits.Kit;
import me.cubecrafter.woolwars.game.powerup.PowerUp;
import me.cubecrafter.woolwars.game.team.Team;
import me.cubecrafter.woolwars.menu.Menu;
import me.cubecrafter.woolwars.utils.ArenaUtil;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ArenaPreRoundTask extends ArenaTask {

    public ArenaPreRoundTask(Arena arena) {
        super(arena);
        arena.setRound(arena.getRound() + 1);
        arena.killEntities();
        arena.resetBlocks();
        arena.getTeams().forEach(Team::spawnBarrier);
        arena.respawnPlayers();
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
            ActionBar.sendActionBarWhile(WoolWars.getInstance(), player, TextUtil.color("&eShift to select a kit!"), () -> arena.getGamePhase().equals(GamePhase.PRE_ROUND));
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
        arena.sendTitle(40, "&a&lROUND START", "&bRound {round}".replace("{round}", String.valueOf(arena.getRound())));
        arena.playSound("BLOCK_ANVIL_LAND");
        arena.getTeams().forEach(Team::removeBarrier);
        arena.getPowerUps().forEach(PowerUp::spawn);
        for (Block block : arena.getWoolRegion().getBlocks()) {
            if (block.hasMetadata("woolwars")) {
                block.removeMetadata("woolwars", WoolWars.getInstance());
            }
        }
        arena.setGamePhase(GamePhase.ACTIVE_ROUND);
    }

    @Override
    public int getTaskDuration() {
        return 10;
    }

}
