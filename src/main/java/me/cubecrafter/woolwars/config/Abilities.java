package me.cubecrafter.woolwars.config;

import lombok.RequiredArgsConstructor;
import me.cubecrafter.woolwars.WoolWars;
import org.bukkit.configuration.ConfigurationSection;

@RequiredArgsConstructor
public enum Abilities {

    GIGAHEAL("default-abilities.gigaheal"),
    KNOCKBACK_TNT("default-abilities.knockback-tnt"),
    STEP_BACK("default-abilities.step-back"),
    SPRINT("default-abilities.sprint"),
    GOLDEN_SHELL("default-abilities.golden-shell"),
    HACK("default-abilities.hack"),
    CUSTOM_ABILITIES("custom-abilities");

    private final String path;

    public ConfigurationSection getAsSection() {
        return WoolWars.getInstance().getFileManager().getAbilities().getConfigurationSection(path);
    }

}
