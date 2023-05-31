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

package me.cubecrafter.woolwars.utils;

import com.cryptomorin.xseries.XPotion;
import de.tr7zw.changeme.nbtapi.NBT;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import me.cubecrafter.woolwars.WoolWars;
import me.cubecrafter.xutils.HttpUtil;
import me.cubecrafter.xutils.Tasks;
import me.cubecrafter.xutils.TextUtil;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.CompletableFuture;

@UtilityClass
public class Utils {

    private static final String UPDATE_CHECK_URL = "https://api.spigotmc.org/legacy/update.php?resource=105548";

    @Getter
    private static String latestVersion = WoolWars.get().getDescription().getVersion();
    @Getter
    private static boolean updateAvailable = false;

    public static CompletableFuture<String> requestInput(Player player, String prompt) {
        CompletableFuture<String> future = new CompletableFuture<>();
        ConversationFactory factory = new ConversationFactory(WoolWars.get());
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

    public static void checkForUpdates() {
        // Check for updates every 60 minutes
        Tasks.repeatAsync(() -> {
            TextUtil.info("Checking for updates...");
            HttpUtil.get(UPDATE_CHECK_URL).thenAccept(response -> {
                if (!response.success()) {
                    TextUtil.warn("Failed to check for updates.");
                    return;
                }
                String current = WoolWars.get().getDescription().getVersion();
                String latest = response.text();
                if (latest.equals(current)) {
                    TextUtil.info("You are running the latest version.");
                    return;
                }
                updateAvailable = true;
                latestVersion = latest;
                TextUtil.info("There is a new update available: " + latest);
            });
        }, 0L, 20 * 60 * 60);
    }

    public static String getCenteredMessage(String message) {
        if (message == null || message.equals("")) return "";
        message = TextUtil.color(message.replace("<center>", "").replace("</center>", ""));
        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;
        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                DefaultFontInfo fontInfo = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? fontInfo.getBoldLength() : fontInfo.getLength();
                messagePxSize++;
            }
        }
        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = 154 - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder builder = new StringBuilder();
        while (compensated < toCompensate) {
            builder.append(" ");
            compensated += spaceLength;
        }
        return builder + message;
    }

    public static PotionEffect parseEffect(String serialized) {
        String[] effect = serialized.split(",");
        PotionEffectType type = XPotion.matchXPotion(effect[0]).orElse(XPotion.SPEED).getPotionEffectType();
        int duration = Integer.parseInt(effect[1]) * 20;
        int amplifier = Integer.parseInt(effect[2]);
        return new PotionEffect(type, duration, amplifier);
    }

    public static String getTag(ItemStack item) {
        if (item == null) {
            return null;
        }
        return NBT.get(item, nbt -> nbt.getString("woolwars"));
    }

}
