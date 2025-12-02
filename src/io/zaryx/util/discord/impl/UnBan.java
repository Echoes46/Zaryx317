package io.zaryx.util.discord.impl;

import io.zaryx.Server;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.entity.player.save.PlayerAddresses;
import io.zaryx.model.entity.player.save.PlayerSaveOffline;
import io.zaryx.util.discord.Discord;
import io.zaryx.util.discord.SlashHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.io.File;

import static io.zaryx.punishments.PunishmentType.*;

/**
 * Slash command: /unban name:<string>
 * Removes account/IP/MAC/UUID bans for the specified player (matches legacy behavior).
 */
public class UnBan extends ListenerAdapter implements SlashHandler{

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (!"unban".equals(e.getName())) {
            return;
        }

        OptionMapping nameOpt = e.getOption("name");
        if (nameOpt == null || nameOpt.getAsString().isBlank()) {
            e.reply("Usage: `/unban name:<player>`").setEphemeral(true).queue();
            return;
        }
        final String name = nameOpt.getAsString().trim();

        // Defer while we do file I/O on the executor
        e.deferReply(true).queue(); // ephemeral

        Server.getIoExecutorService().submit(() -> {
            try {
                File file = PlayerSaveOffline.getCharacterFile(name);
                if (file == null) {
                    Discord.writeGiveLog("[UnBan-log] No character file with name " + name);
                    e.getHook().editOriginal("No character file found for `" + name + "`.").queue();
                    return;
                }

                boolean accountWasBanned = Server.getPunishments().remove(BAN, name);
                if (!accountWasBanned) {
                    Discord.writeGiveLog("[UnBan-log] " + name + " isn't banned.");
                } else {
                    Discord.writeGiveLog("[UnBan-log] " + e.getUser().getName() + " has unbanned " + name);
                }

                PlayerAddresses addresses = PlayerSaveOffline.getAddresses(file);
                if (addresses != null) {
                    // Match your original removals
                    PlayerHandler.addQueuedAction(() -> {
                        if (addresses.getIp() != null)   Server.getPunishments().remove(NET_BAN, addresses.getIp());
                        if (addresses.getMac() != null)  Server.getPunishments().remove(MAC_BAN, addresses.getMac());
                        if (addresses.getUUID() != null) Server.getPunishments().remove(MAC_BAN, addresses.getUUID());
                    });
                }

                e.getHook().editOriginal(
                        (accountWasBanned ? "✅ Removed BAN for `" + name + "`." : "ℹ️ `" + name + "` was not account-banned.") +
                                (addresses != null ? " Cleared related NET/MAC/UUID bans where present." : " No address info found.")
                ).queue();

            } catch (Exception ex) {
                ex.printStackTrace();
                e.getHook().editOriginal("An error occurred while unbanning `" + name + "`.").queue();
            }
        });
    }
    @Override
    public void handle(SlashCommandInteractionEvent e) {
        onSlashCommandInteraction(e);
    }
}
