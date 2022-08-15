package me.cubecrafter.woolwars.listeners;

import me.cubecrafter.woolwars.api.events.arena.GameEndEvent;
import me.cubecrafter.woolwars.api.events.arena.RoundEndEvent;
import me.cubecrafter.woolwars.api.events.player.PlayerKillEvent;
import me.cubecrafter.woolwars.config.Configuration;
import me.cubecrafter.woolwars.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class RewardsListener implements Listener {

    @EventHandler
    public void onRoundEnd(RoundEndEvent e) {
        for (Team team : e.getLoserTeams()) {
            team.getMembers().forEach(player -> executeCommands(player, Configuration.REWARD_COMMANDS_ROUND_LOSE.getAsStringList()));
        }
        e.getWinnerTeam().getMembers().forEach(player -> executeCommands(player, Configuration.REWARD_COMMANDS_ROUND_WIN.getAsStringList()));
    }

    @EventHandler
    public void onKillDeath(PlayerKillEvent e) {
        executeCommands(e.getVictim(), Configuration.REWARD_COMMANDS_DEATH.getAsStringList());
        if (e.getAttacker() == null) return;
        executeCommands(e.getAttacker(), Configuration.REWARD_COMMANDS_KILL.getAsStringList());
    }

    @EventHandler
    public void onGameEnd(GameEndEvent e) {
        for (Team team : e.getLoserTeams()) {
            team.getMembers().forEach(player -> executeCommands(player, Configuration.REWARD_COMMANDS_MATCH_LOSE.getAsStringList()));
        }
        e.getWinnerTeam().getMembers().forEach(player -> executeCommands(player, Configuration.REWARD_COMMANDS_MATCH_WIN.getAsStringList()));
    }

    public void executeCommands(Player player, List<String> commands) {
        for (String command : commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName()).replace("{uuid}", player.getUniqueId().toString()));
        }
    }

}
