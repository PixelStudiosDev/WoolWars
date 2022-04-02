package me.cubecrafter.woolwars.core.tasks;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.Titles;
import lombok.Getter;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.core.Arena;
import me.cubecrafter.woolwars.core.GameState;
import me.cubecrafter.woolwars.core.Team;
import me.cubecrafter.woolwars.menu.menus.KitsMenu;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class ArenaStartingTask implements Runnable {

    @Getter private final BukkitTask task;
    private final Arena arena;
    private int countdown = 5;

    public ArenaStartingTask(Arena arena) {
        this.arena = arena;
        task = Bukkit.getScheduler().runTaskTimer(WoolWars.getInstance(), this, 0L, 20L);
    }

    @Override
    public void run() {
        if (countdown == 0) {
            arena.setGameState(GameState.SELECTING_KIT);
            arena.assignTeams();
            for (Team team : arena.getTeams().values()) {
                team.setNameTags();
                team.applyArmor();
                team.teleportToSpawn();
            }
            for (Player player : arena.getPlayers()) {
                Titles.sendTitle(player, 0, 40, 0, TextUtil.color("&e&lPRE ROUND"), TextUtil.color("&bSelect your kit!"));
                XSound.play(player, "BLOCK_ANVIL_LAND");
                new KitsMenu(player).openMenu();
            }
            task.cancel();
        } else {
            arena.broadcast(TextUtil.color("&eThe game starts in &c{seconds} &eseconds!".replace("{seconds}", String.valueOf(countdown))));
            for (Player player : arena.getPlayers()) {
                XSound.play(player, "ENTITY_CHICKEN_EGG");
            }
            countdown--;
        }
    }

}
