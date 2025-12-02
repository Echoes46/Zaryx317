package io.zaryx.util.discord.impl;

import io.zaryx.content.commands.helper.vboss;
import io.zaryx.util.discord.Discord;
import io.zaryx.util.discord.SlashHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Slash command: /voteboss
 * Spawns the Vote Boss and logs the action.
 */
public class VoteBoss extends ListenerAdapter implements SlashHandler{

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (!"voteboss".equals(e.getName())) {
            return;
        }

        // Run your in-game logic
        vboss.spawnBoss();

        // Log + confirm
        String staffName = e.getUser().getName();
        Discord.writeGiveLog("[Vote Boss] " + staffName + " has spawned Vote Boss!");
        e.reply("🗳️ **Vote Boss spawned** by **" + staffName + "**!").queue();
    }
    @Override
    public void handle(SlashCommandInteractionEvent e) {
        onSlashCommandInteraction(e);
    }
}
