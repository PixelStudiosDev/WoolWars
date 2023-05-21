/*
 * Wool Wars
 * Copyright (C) 2023 CubeCrafter Development
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

package me.cubecrafter.woolwars.kit.ability;

import lombok.Getter;
import me.cubecrafter.woolwars.api.events.player.PlayerAbilityEvent;
import me.cubecrafter.woolwars.arena.Arena;
import me.cubecrafter.woolwars.arena.GameState;
import me.cubecrafter.woolwars.config.Messages;
import me.cubecrafter.woolwars.storage.player.WoolPlayer;
import me.cubecrafter.woolwars.utils.ItemBuilder;
import me.cubecrafter.xutils.Events;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

@Getter
public abstract class Ability {

    private final Type type;
    private final String id;
    private final String name;

    private final ItemStack item;
    private final int slot;

    protected final ConfigurationSection section;

    public Ability(ConfigurationSection section, Type type) {
        this.section = section;
        this.type = type;
        this.id = section.getName();
        this.name = section.getString("name");
        // Inventory item
        this.item = ItemBuilder.fromConfig(section.getConfigurationSection("item")).setTag("ability").build();
        this.slot = section.getInt("slot");
    }

    public void use(WoolPlayer player, Arena arena) {
        if (arena.getState() != GameState.ACTIVE_ROUND) {
            player.send(Messages.ABILITY_CANT_USE.asString());
            return;
        }
        if (player.isAbilityUsed()) {
            player.send(Messages.ABILITY_ALREADY_USED.asString());
            return;
        }
        if (Events.call(new PlayerAbilityEvent(player, arena, this))) return;
        // Handle the ability
        if (handle(player, arena)) {
            player.setAbilityUsed(true);
            player.send(Messages.ABILITY_USE.asString().replace("{name}", name));
        }
    }

    protected abstract boolean handle(WoolPlayer player, Arena arena);

    public enum Type {
        GIGAHEAL, GOLDEN_SHELL, HACK, KNOCKBACK_TNT, SPRINT, STEP_BACK, CUSTOM
    }

    public static Ability fromConfig(ConfigurationSection section) {
        Type type = Type.valueOf(section.getString("type").toUpperCase());
        switch (type) {
            case GIGAHEAL:
                return new GigaHealAbility(section);
            case GOLDEN_SHELL:
                return new GoldenShellAbility(section);
            case HACK:
                return new HackAbility(section);
            case KNOCKBACK_TNT:
                return new KnockbackTNTAbility(section);
            case SPRINT:
                return new SprintAbility(section);
            case STEP_BACK:
                return new StepBackAbility(section);
            case CUSTOM:
                return new CustomAbility(section);
        }
        return null;
    }

}
