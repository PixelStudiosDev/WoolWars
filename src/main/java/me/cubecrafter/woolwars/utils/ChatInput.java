package me.cubecrafter.woolwars.utils;

import me.cubecrafter.woolwars.WoolWars;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class ChatInput {

    public ChatInput(Player player, Consumer<String> onInput) {
        ConversationFactory factory = new ConversationFactory(WoolWars.getInstance());
        factory.withLocalEcho(false);
        factory.withFirstPrompt(new StringPrompt() {
            @Override
            public String getPromptText(ConversationContext context) {
                return "";
            }
            @Override
            public Prompt acceptInput(ConversationContext context, String input) {
                onInput.accept(input);
                return Prompt.END_OF_CONVERSATION;
            }
        });
        factory.buildConversation(player).begin();
    }

}
