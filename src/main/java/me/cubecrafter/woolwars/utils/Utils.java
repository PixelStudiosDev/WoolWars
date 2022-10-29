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

package me.cubecrafter.woolwars.utils;

import me.cubecrafter.woolwars.WoolWars;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class Utils {

    public static CompletableFuture<String> requestInput(Player player, String prompt) {
        CompletableFuture<String> future = new CompletableFuture<>();
        ConversationFactory factory = new ConversationFactory(WoolWars.getInstance());
        factory.withLocalEcho(false);
        factory.withFirstPrompt(new StringPrompt() {
            @Override
            public String getPromptText(ConversationContext context) {
                return TextUtil.color(prompt);
            }
            @Override
            public Prompt acceptInput(ConversationContext context, String input) {
                future.complete(input);
                return Prompt.END_OF_CONVERSATION;
            }
        });
        factory.buildConversation(player).begin();
        return future;
    }

}
