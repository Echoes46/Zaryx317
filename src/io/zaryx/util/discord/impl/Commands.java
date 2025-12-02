package io.zaryx.util.discord.impl;

import io.zaryx.util.discord.SlashHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

/**
 * Slash command: /commands
 * Shows an embed with the available bot commands.
 */
public class Commands extends ListenerAdapter  implements SlashHandler {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (!"commands".equals(e.getName())) {
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Command List");
        // pick one color (you can change this to any RGB you like)
        eb.setColor(new Color(0xF40C0C));

        // Update names to your actual slash-command names
        eb.addField("/giveitem <user> <id> <amount>", "Give an item to a player", true);
        eb.addField("/tpdelete <user>", "Removes a player's Trading post listing", true);
        eb.addField("/ban <user>", "Network bans a user", true);
        eb.addField("/unban <user>", "Removes network ban from user", true);
        eb.addField("/mute <user>", "Network mutes a user", true);
        eb.addField("/jail <user>", "Jails a user", true);
        eb.addField("/unmute <user>", "Unmutes a user", true);
        eb.addField("/unjail <user>", "Unjails a user", true);

        eb.addField("/groot", "Spawns Groot", true);
        eb.addField("/voteboss", "Spawns Vote Boss", true);
        eb.addField("/donorboss", "Spawns Donor Boss", true);

        eb.addField("/offlinereward <user> <id> <amount>", "Gives user their reward to offline box", true);
        eb.addField("/wealthplat", "Check players' plat worth", true);
        eb.addField("/wealthcoins", "Check players' coins worth", true);
        eb.addField("/wealthupgrade", "Check players' upgrader worth", true);

        eb.addField("/currentevents", "Displays current in-game events", true);
        eb.addField("/listonlineplayers", "Lists all online players", true);
        eb.addField("/connectaccount", "Link your Discord and in-game account", true);

        eb.setAuthor("Lumen");

        // Reply with the embed (make it non-ephemeral so everyone can see, or setEphemeral(true) if you prefer)
        e.replyEmbeds(eb.build()).queue();
    }
    @Override
    public void handle(SlashCommandInteractionEvent e) {
        onSlashCommandInteraction(e);
    }
}
