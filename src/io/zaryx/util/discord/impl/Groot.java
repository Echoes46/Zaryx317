package io.zaryx.util.discord.impl;

import io.zaryx.util.discord.Discord;
import io.zaryx.util.discord.SlashHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Slash command: /groot
 * Spawns the Groot activity boss and logs the action.
 */
public class Groot extends ListenerAdapter implements SlashHandler {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (!"groot".equals(e.getName())) {
            return;
        }

        String staffName = e.getUser().getName();

        io.zaryx.content.activityboss.Groot.spawnGroot();

        Discord.writeGiveLog("[Groot] " + staffName + " has spawned Groot!");

        e.reply("Groot spawned by " + staffName + ".").queue();
    }
    @Override
    public void handle(SlashCommandInteractionEvent e) {
        onSlashCommandInteraction(e);
    }
}
