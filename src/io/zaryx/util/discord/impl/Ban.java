package io.zaryx.util.discord.impl;

import io.zaryx.Server;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.entity.player.save.PlayerAddresses;
import io.zaryx.model.multiplayersession.MultiplayerSession;
import io.zaryx.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.zaryx.punishments.Punishment;
import io.zaryx.punishments.PunishmentType;
import io.zaryx.util.discord.Discord;
import io.zaryx.util.discord.SlashHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Slash command: /ban name:<string>
 * Bans a player (and any clients on the same IP), logs the action, and forces logout.
 */
public class Ban extends ListenerAdapter  implements SlashHandler {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (!"ban".equals(e.getName())) {
            return;
        }

        // Get arguments
        OptionMapping nameOpt = e.getOption("name");
        if (nameOpt == null || nameOpt.getAsString().isBlank()) {
            e.reply("You must provide a player name. Usage: `/ban name:<player>`")
                    .setEphemeral(true).queue();
            return;
        }
        final String name = nameOpt.getAsString().trim();

        // Find player by display name
        Player player = PlayerHandler.getPlayerByDisplayName(name);
        if (player == null) {
            Discord.writeGiveLog("[Ban-Log] " + name + " is not online.");
            e.reply("Player `" + name + "` is not online.").setEphemeral(true).queue();
            return;
        }

        // Gather addresses and issue punishments
        PlayerAddresses addresses = player.getValidAddresses();
        if (addresses == null || addresses.getIp() == null || addresses.getIp().isBlank()) {
            e.reply("Could not determine player's IP address. Aborting.").setEphemeral(true).queue();
            return;
        }

        String staffName = e.getUser().getName();

        // Network ban by IP
        Server.getPunishments().add(new Punishment(PunishmentType.NET_BAN, Long.MAX_VALUE, addresses.getIp()));

        // Notify player & log
        player.sendMessage(player.getDisplayName() + ", you have been banned by: " + staffName);
        Discord.writeGiveLog("[Ban-Log] " + staffName + " banned " + player.getDisplayName() + " (IP: " + addresses.getIp() + ")");

        // Find all connected players on the same IP and ban/logout
        List<Player> sameIpClients = PlayerHandler.nonNullStream()
                .filter(p -> Objects.equals(p.connectedFrom, addresses.getIp()))
                .collect(Collectors.toList());

        for (Player pz : sameIpClients) {
            // Username ban
            Server.getPunishments().add(new Punishment(PunishmentType.BAN, Long.MAX_VALUE, pz.getLoginName()));

            // If in any session, finalize appropriately
            if (Server.getMultiplayerSessionListener().inAnySession(pz)) {
                MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(pz);
                if (session != null) {
                    session.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
                }
            }

            // Force logout
            pz.forceLogout();
        }

        e.reply("Banned **" + player.getDisplayName() + "** and disconnected "
                + sameIpClients.size() + " client(s) on IP `" + addresses.getIp() + "`.").queue();
    }
    @Override
    public void handle(SlashCommandInteractionEvent e) {
        onSlashCommandInteraction(e);
    }
}
