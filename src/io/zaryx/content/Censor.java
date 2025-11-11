package io.zaryx.content;

import com.google.common.collect.Lists;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.util.discord.DiscordBot;
import io.zaryx.util.discord.DiscordChannelType;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.List;

public class Censor {

    private static final List<String> CENSORED = Lists.newArrayList(
            "wisdomrsps", "vengeanceps",  "redemptionrsps", "ikov", "zaros",
            "nigger", "nigga", "niger", "niggr", "nigr", "niggar", "niggas", "niggers", "nggr", 
            "pedophile", "pedofile", "stakescape", "(dot)"
    );

    public static boolean isCensoredName(String name) {
        String _name = name.toLowerCase();
        return CENSORED.stream().anyMatch(_name::contains);
    }

    public static boolean isCensored(Player player, String message) {
        if (player.getRights().hasStaffPosition()) {
            return false;
        }

        message = message.toLowerCase();
        for (String censor : CENSORED) {
            if (message.contains(censor) && !censor.equalsIgnoreCase("org")) {
                if (DiscordBot.INSTANCE != null) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle(" [ CENSOR CHECK ] ");
                    embed.setColor(Color.BLUE);
                    embed.setTimestamp(java.time.Instant.now());
                    embed.addField("Player: ", player.getDisplayName() + " was muted for 5 minutes for saying: " + censor, false);
                    DiscordBot.INSTANCE.sendStaffLogs(embed.build());
                    DiscordBot.INSTANCE.sendMessage(DiscordChannelType.STAFF_LOGS, "@everyone");
                }
//                player.sendMessage("@red@[WARNING]@bla@ You are not allowed to say " + censor +", you have been muted for 5 minutes.");
                player.sendMessage("@red@[WARNING]@bla@ Staff have been notified, please do not use this language again.");
                PlayerHandler.executeGlobalStaffMessage("[@red@Staff Message@bla@] <col=255>" + player.getDisplayName() + " has been muted for saying "+ censor +"");

                player.muteEnd = System.currentTimeMillis() + (60_000 * 5);
                return true;
            }
        }

        return false;
    }

}
