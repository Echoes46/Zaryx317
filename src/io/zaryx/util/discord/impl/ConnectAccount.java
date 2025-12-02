package io.zaryx.util.discord.impl;

import io.zaryx.util.discord.Discord;
import io.zaryx.util.discord.DiscordIntegration;
import io.zaryx.util.discord.SlashHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Map;

/**
 * /connectaccount
 * Generates a short code and DMs the user instructions to link their Discord to their in-game account.
 */
public class ConnectAccount implements SlashHandler {

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        if (!"connectaccount".equals(event.getName())) return;

        // ---- Permission gate (supports & up) ----
        Member member = event.getMember();
        String[] allowedRoleIds = {
                Discord.SUPPORT_ROLE,
                Discord.GLOBAL_MOD_ROLE,
                Discord.ADMIN_ROLE,
                Discord.MANAGER_ROLE,
                Discord.DEVELOPER_ROLE,
                Discord.OWNER_ROLE,
                Discord.MEMBER_ROLE
        };
        if (!hasRequiredRole(member, allowedRoleIds)) {
            event.reply("You do not have permission to use `/connectaccount`.").setEphemeral(true).queue();
            return;
        }

        // Acknowledge publicly (ephemeral), then continue via DM
        event.reply("Hello Adventurer! Check your DMs — I just slid in!").setEphemeral(true).queue();

        final User user = event.getUser();
        final long userId = user.getIdLong();

        // Prefer a guild text channel when calling your helper (if available)
        TextChannel replyChannel = null;
        try {
            if (event.isFromGuild()) {
                replyChannel = event.getChannel().asTextChannel();
            }
        } catch (Throwable ignored) { }

        // Already connected?
        if (DiscordIntegration.connectedAccounts != null && DiscordIntegration.connectedAccounts.containsValue(userId)) {
            DiscordIntegration.sendPrivateMessage(user, replyChannel,
                    "This Discord account is already connected to another in-game account!");
            return;
        }

        // If a code already exists for this user, reuse it
        String existing = findExistingCodeForUser(userId);
        if (existing != null) {
            DiscordIntegration.sendPrivateMessage(
                    user,
                    replyChannel,
                    "To link your Discord:\n" +
                            "1) In-game, type **`::linkdiscord`** (or open the Discord panel > **Sync**)\n" +
                            "2) When prompted, enter this code:\n" +
                            "**`" + existing + "`**"
            );
            return;
        }

        // Generate a fresh unique code (safe alphanumeric, uppercase)
        String codeKey = DiscordIntegration.generateCode(6);
        while (DiscordIntegration.idForCode.containsKey(codeKey)) {
            codeKey = DiscordIntegration.generateCode(6);
        }
        DiscordIntegration.idForCode.put(codeKey, userId);

        DiscordIntegration.sendPrivateMessage(
                user,
                replyChannel,
                "To link your Discord:\n" +
                        "1) In-game, type **`::linkdiscord`** (or open the Discord panel > **Sync**)\n" +
                        "2) When prompted, enter this code:\n" +
                        "**`" + codeKey + "`**"
        );
    }

    // ----- helpers -----

    private boolean hasRequiredRole(Member member, String[] roleIds) {
        if (member == null || roleIds == null || roleIds.length == 0) return false;
        for (Role role : member.getRoles()) {
            final String id = role.getId();
            for (String need : roleIds) {
                if (id.equals(need)) return true;
            }
        }
        return false;
    }

    private String findExistingCodeForUser(long userId) {
        for (Map.Entry<String, Long> e : DiscordIntegration.idForCode.entrySet()) {
            if (e.getValue() != null && e.getValue() == userId) {
                return e.getKey(); // keys are already uppercase alphanumeric
            }
        }
        return null;
    }
}
