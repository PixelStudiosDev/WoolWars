package me.cubecrafter.woolwars.hooks;

import lombok.Getter;
import me.cubecrafter.woolwars.utils.TextUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

    @Getter Economy economy;

    public VaultHook() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            economy = Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        } else {
            TextUtil.severe("No economy provider plugin!");
        }
    }

}
