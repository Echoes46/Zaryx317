package io.zaryx.util.discord.impl;

import io.zaryx.content.commands.admin.dboss;
import io.zaryx.util.discord.Discord;
import io.zaryx.util.discord.SlashHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Slash command: /donorboss
 * Spawns the Donor Boss in-game and logs the action to Discord.
 */
public class DonorBoss extends ListenerAdapter implements SlashHandler {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (!"donorboss".equals(e.getName())) {
            return;
        }

        // Call your in-game logic
        dboss.spawnBoss();

        // Log the action
        String staffName = e.getUser().getName();
        Discord.writeGiveLog("[Donor Boss] " + staffName + " has spawned Donor Boss!");

        // Confirm in Discord
        e.reply("✅ Donor Boss has been spawned by **" + staffName + "**!").queue();
    }
    @Override
    public void handle(SlashCommandInteractionEvent e) {
        onSlashCommandInteraction(e);
    }
}
