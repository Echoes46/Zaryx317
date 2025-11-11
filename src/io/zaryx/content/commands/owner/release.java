package io.zaryx.content.commands.owner;

import io.zaryx.Server;
import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.discord.DiscordBot;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class release extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        Server.ServerLocked = !Server.ServerLocked;
        player.sendMessage("@red@The server is now " + (!Server.ServerLocked ? "unlocked" : "locked") + "!");

        if (!Server.ServerLocked) {
            if (DiscordBot.INSTANCE != null) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("[ SERVER STATUS ]");
                embed.setImage("https://oldschool.runescape.wiki/images/Zaros_symbol.png?bd133");
                embed.setColor(Color.GREEN);
                embed.setTimestamp(java.time.Instant.now());
                embed.addField("Server is now online.", "\\u200B", false);
                DiscordBot.INSTANCE.sendServerStatus(embed.build());
                //DiscordBot.INSTANCE.sendMessage(DiscordChannelType.SERVER_STATUS, "@everyone");
            }
        }
    }
}
