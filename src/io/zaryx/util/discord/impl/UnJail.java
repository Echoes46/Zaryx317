package io.zaryx.util.discord.impl;

import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.util.discord.Discord;
import io.zaryx.util.discord.SlashHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

/**
 * Slash command: /unjail name:<string>
 * Moves the player out of jail and clears jail state.
 */
public class UnJail extends ListenerAdapter implements SlashHandler{

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (!"unjail".equals(e.getName())) return;

        OptionMapping nameOpt = e.getOption("name");
        if (nameOpt == null || nameOpt.getAsString().isBlank()) {
            e.reply("Usage: `/unjail name:<player>`").setEphemeral(true).queue();
            return;
        }

        final String name = nameOpt.getAsString().trim();
        Player p = PlayerHandler.getPlayerByDisplayName(name);

        if (p == null) {
            // keep your original logging vibe
            Discord.writeGiveLog("[JAIL] " + name + " has clearly fucking logged out, or you're an idiot and can't spell for shit, I'm telling winemaker you fucking idiot.");
            e.reply("`" + name + "` is not online.").setEphemeral(true).queue();
            return;
        }

        // Move player out of jail + clear flags
        p.getPA().movePlayer(3093, 3493, 0); // same coords as your legacy code
        p.jailEnd = 0;
        p.isStuck = false;

        String staff = e.getUser().getName();
        p.sendMessage("You have been unjailed by " + staff + ". Don't get jailed again!");
        Discord.writeGiveLog("[JAIL] " + staff + " has unJailed " + p.getLoginName() + "/" + p.getDisplayName());

        e.reply("🔓 Unjailed **" + p.getDisplayName() + "**.").queue();
    }
    @Override
    public void handle(SlashCommandInteractionEvent e) {
        onSlashCommandInteraction(e);
    }
}
