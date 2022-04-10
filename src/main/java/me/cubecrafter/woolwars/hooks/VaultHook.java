package me.cubecrafter.woolwars.hooks;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;

public class VaultHook {

    @Getter Economy economy;

    public VaultHook() {
        economy = Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
    }

}
