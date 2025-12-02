package io.zaryx.util.discord.impl;

import io.zaryx.Server;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.entity.player.save.PlayerAddresses;
import io.zaryx.punishments.PunishmentType;
import io.zaryx.util.discord.Discord;
import io.zaryx.util.discord.SlashHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

/**
 * Slash command: /unmute name:<string>
 * Unmutes a player (clears help-cc mute & removes NET_MUTE for IP/MAC/UUID).
 */
public class UnMute extends ListenerAdapter implements SlashHandler{

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (!"unmute".equals(e.getName())) {
            return;
        }

        OptionMapping nameOpt = e.getOption("name");
        if (nameOpt == null || nameOpt.getAsString().isBlank()) {
            e.reply("Usage: `/unmute name:<player>`").setEphemeral(true).queue();
            return;
        }
        final String name = nameOpt.getAsString().trim();

        Player p = PlayerHandler.getPlayerByDisplayName(name);
        if (p == null) {
            // keep the spirit of your legacy log line
            Discord.writePunishmentLog("[Mute-log] Well it's come to my attention that either they don't exist or you have a serious spelling issue, you fucktard.");
            e.reply("`" + name + "` is not online.").setEphemeral(true).queue();
            return;
        }

        // Clear local mute state
        p.setHelpCcMuted(false);
        p.muteEnd = 0;

        // Remove network mutes from stored addresses
        PlayerAddresses addresses = p.getValidAddresses();
        if (addresses != null) {
            if (addresses.getIp() != null) {
                Server.getPunishments().remove(PunishmentType.NET_MUTE, addresses.getIp());
            }
            if (addresses.getMac() != null) {
                Server.getPunishments().remove(PunishmentType.NET_MUTE, addresses.getMac());
            }
            if (addresses.getUUID() != null && p.getUUID() != null && !p.getUUID().isEmpty()) {
                Server.getPunishments().remove(PunishmentType.NET_MUTE, addresses.getUUID());
            }
        }

        String staff = e.getUser().getName();
        Discord.writePunishmentLog("[Mute-log] " + p.getDisplayName() + " has been unmuted by " + staff);
        p.sendMessage("You have been unmuted by " + staff + ".");
        e.reply("🔊 Unmuted **" + p.getDisplayName() + "**.").queue();
    }
    @Override
    public void handle(SlashCommandInteractionEvent e) {
        onSlashCommandInteraction(e);
    }
}
