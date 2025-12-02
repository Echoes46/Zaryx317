package io.zaryx.util.discord.impl;

import io.zaryx.Server;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.entity.player.save.PlayerAddresses;
import io.zaryx.punishments.PunishmentType;
import io.zaryx.util.dateandtime.TimeSpan;
import io.zaryx.util.discord.Discord;
import io.zaryx.util.discord.SlashHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.concurrent.TimeUnit;

/**
 * /netmute name:<player>
 * Network-mutes a player for 1 day (IP/MAC/UUID), matching the behavior/logging of your Mute command.
 */
public class NetMute implements SlashHandler {

    @Override
    public void handle(SlashCommandInteractionEvent e) {
        if (!"netmute".equals(e.getName())) return;

        // required option: name
        OptionMapping nameOpt = e.getOption("name");
        if (nameOpt == null || nameOpt.getAsString().isBlank()) {
            e.reply("Usage: `/netmute name:<player>`").setEphemeral(true).queue();
            return;
        }

        final String name = nameOpt.getAsString().trim();
        Player player = PlayerHandler.getPlayerByDisplayName(name);
        if (player == null) {
            // same text as your Mute command for consistency
            Discord.writePunishmentLog("[Mute-Log] Oh you're a special one aren't you, Either they don't exist or they're offline.");
            e.reply("`" + name + "` is not online.").setEphemeral(true).queue();
            return;
        }

        // 1-day network mute across identifiers
        TimeSpan timeSpan = new TimeSpan(TimeUnit.DAYS, 1);
        PlayerAddresses addresses = player.getValidAddresses();
        if (addresses != null) {
            if (addresses.getIp() != null) {
                Server.getPunishments().add(PunishmentType.NET_MUTE, timeSpan, addresses.getIp());
            }
            if (addresses.getMac() != null) {
                Server.getPunishments().add(PunishmentType.NET_MUTE, timeSpan, addresses.getMac());
            }
            if (addresses.getUUID() != null) {
                Server.getPunishments().add(PunishmentType.NET_MUTE, timeSpan, addresses.getUUID());
            }
        }

        String staff = e.getUser().getName();
        player.sendMessage(player.getDisplayName() + ", you have been muted by: " + staff);

        // same channel/log call as your Mute
        Discord.writePunishmentLog("[Mute-Log] " + staff + " muted " + player.getDisplayName());
        e.reply("🔇 Muted **" + player.getDisplayName() + "** for 1 day.").queue();
    }
}
