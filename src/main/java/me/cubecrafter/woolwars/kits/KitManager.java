/*
 * Wool Wars
 * Copyright (C) 2022 CubeCrafter Development
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.cubecrafter.woolwars.kits;

import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.woolwars.config.Abilities;
import me.cubecrafter.woolwars.kits.ability.Ability;
import me.cubecrafter.woolwars.kits.ability.CustomAbility;
import me.cubecrafter.woolwars.kits.ability.GigaHealAbility;
import me.cubecrafter.woolwars.kits.ability.GoldenShellAbility;
import me.cubecrafter.woolwars.kits.ability.HackAbility;
import me.cubecrafter.woolwars.kits.ability.KnockbackTNTAbility;
import me.cubecrafter.woolwars.kits.ability.SprintAbility;
import me.cubecrafter.woolwars.kits.ability.StepBackAbility;
import me.cubecrafter.woolwars.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KitManager {

    private final Map<String, Kit> kits = new HashMap<>();
    private final Map<String, Ability> abilities = new HashMap<>();
    private final Set<Player> abilityCooldowns = new HashSet<>();
    private final WoolWars plugin;

    public KitManager(WoolWars plugin) {
        this.plugin = plugin;
    }

    public Collection<Kit> getKits() {
        return kits.values();
    }

    public Kit getKit(String id) {
        return kits.get(id);
    }

    public Ability getAbility(String id) {
        return abilities.get(id.toUpperCase());
    }

    public void load() {
        kits.clear();
        abilities.clear();
        abilities.put("GIGAHEAL", new GigaHealAbility(Abilities.GIGAHEAL.getAsSection()));
        abilities.put("KNOCKBACK_TNT", new KnockbackTNTAbility(Abilities.KNOCKBACK_TNT.getAsSection()));
        abilities.put("STEP_BACK", new StepBackAbility(Abilities.STEP_BACK.getAsSection()));
        abilities.put("SPRINT", new SprintAbility(Abilities.SPRINT.getAsSection()));
        abilities.put("GOLDEN_SHELL", new GoldenShellAbility(Abilities.GOLDEN_SHELL.getAsSection()));
        abilities.put("HACK", new HackAbility(Abilities.HACK.getAsSection()));
        int loadedAbilities = 0;
        for (String id : Abilities.CUSTOM_ABILITIES.getAsSection().getKeys(false)) {
            ConfigurationSection section = Abilities.CUSTOM_ABILITIES.getAsSection().getConfigurationSection(id);
            Ability ability = new CustomAbility(section);
            abilities.put(id.toUpperCase(), ability);
            loadedAbilities++;
        }
        TextUtil.info("Loaded " + loadedAbilities + " custom abilities!");
        int loadedKits = 0;
        for (File kitFile : plugin.getFileManager().getKitFiles()) {
            String id = kitFile.getName().replace(".yml", "");
            YamlConfiguration kitConfig = YamlConfiguration.loadConfiguration(kitFile);
            Kit kit = new Kit(id, kitConfig);
            kits.put(id, kit);
            TextUtil.info("Kit '" + id + "' loaded!");
            loadedKits++;
        }
        TextUtil.info("Loaded " + loadedKits + " kits!");
    }

    public boolean hasCooldown(Player player) {
        return abilityCooldowns.contains(player);
    }

    public void addCooldown(Player player) {
        abilityCooldowns.add(player);
    }

    public void removeCooldown(Player player) {
        abilityCooldowns.remove(player);
    }

}
