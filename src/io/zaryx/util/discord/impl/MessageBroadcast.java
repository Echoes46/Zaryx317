package io.zaryx.util.discord.impl;

import io.zaryx.model.entity.player.broadcasts.Broadcast;
import io.zaryx.util.discord.Discord;
import io.zaryx.util.discord.SlashHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

/**
 * /message
 * Broadcasts a server-wide message in-game.
 */
public class MessageBroadcast implements SlashHandler {

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        if (!"message".equals(event.getName())) return;

        // ---- Permission gate: admin/dev/manager/owner ----
        Member member = event.getMember();
        String[] allowedRoleIds = {
                Discord.ADMIN_ROLE,
                Discord.MANAGER_ROLE,
                Discord.DEVELOPER_ROLE,
                Discord.OWNER_ROLE
        };
        if (!hasRequiredRole(member, allowedRoleIds)) {
            event.reply("You do not have permission to use `/message`.").setEphemeral(true).queue();
            return;
        }

        // ---- Read required "message" option ----
        OptionMapping msgOpt = event.getOption("message");
        if (msgOpt == null || msgOpt.getAsString().isBlank()) {
            event.reply("Usage: `/message message:<text>`").setEphemeral(true).queue();
            return;
        }
        String text = msgOpt.getAsString().trim();

        try {
            new Broadcast(text).submit(); // ✅ directly submit to broadcast system
            event.reply("📣 Broadcasting: `" + text + "`").setEphemeral(true).queue();
        } catch (Throwable t) {
            event.reply("❌ Failed to broadcast that message. Check server logs.").setEphemeral(true).queue();
        }
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
}
