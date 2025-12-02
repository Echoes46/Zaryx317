package io.zaryx.util.discord.impl;

import io.zaryx.Server;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.util.dateandtime.TimeSpan;
import io.zaryx.util.discord.Discord;
import io.zaryx.util.discord.SlashHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.concurrent.TimeUnit;

/**
 * Slash command: /jail name:<string>
 * Jails a player for 5 years (matching your original behavior), with trade/duel guard.
 */
public class Jail extends ListenerAdapter implements SlashHandler {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (!"jail".equals(e.getName())) {
            return;
        }

        OptionMapping nameOpt = e.getOption("name");
        if (nameOpt == null || nameOpt.getAsString().isBlank()) {
            e.reply("Usage: `/jail name:<player>`").setEphemeral(true).queue();
            return;
        }
        final String name = nameOpt.getAsString().trim();

        Player p = PlayerHandler.getPlayerByDisplayName(name);
        if (p == null) {
            // keep your original… flavor 😅
            Discord.writeGiveLog("[JAIL] " + name + " has clearly fucking logged out, or you're an idiot and can't spell for shit, I'm telling winemaker you fucking idiot.");
            e.reply("`" + name + "` is not online.").setEphemeral(true).queue();
            return;
        }

        if (Server.getMultiplayerSessionListener().inAnySession(p)) {
            Discord.writeGiveLog("[JAIL] The player is in a trade, or duel. You cannot do this at this time.");
            e.reply("Player is currently in a trade/duel. Try again later.").setEphemeral(true).queue();
            return;
        }

        // 5-year jail (matches your original)
        TimeSpan timeSpan = new TimeSpan(TimeUnit.DAYS, TimeUnit.DAYS.toMinutes(365 * 5));

        // Teleport to jail + set jail end time
        p.setTeleportToX(3610);
        p.setTeleportToY(3676);
        p.heightLevel = 0;
        p.jailEnd = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(365L * 5L);

        // Notify player + log
        String staff = e.getUser().getName();
        p.sendMessage("@red@You have been jailed by " + staff + " for a duration of " + timeSpan.toString());
        Discord.writeGiveLog("[JAIL] " + staff + " has jailed " + p.getLoginName() + "/" + p.getDisplayName()
                + " for a duration of " + timeSpan.toString());

        // Acknowledge command
        e.reply("🚓 Jailed **" + p.getDisplayName() + "** for **" + timeSpan.toString() + "**.").queue();
    }
    @Override
    public void handle(SlashCommandInteractionEvent e) {
        onSlashCommandInteraction(e);
    }
}
