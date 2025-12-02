package io.zaryx.util.discord.impl;

import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.util.discord.Discord;
import io.zaryx.util.discord.SlashHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SendHome implements SlashHandler {

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        if (!"sendhome".equals(event.getName())) return;

        // ---- permission gate (owner/dev/manager/admin) ----
        Member member = event.getMember();
        String[] allowedRoles = {
                Discord.OWNER_ROLE,
                Discord.DEVELOPER_ROLE,
                Discord.MANAGER_ROLE,
                Discord.ADMIN_ROLE
        };
        if (!hasRequiredRole(member, allowedRoles)) {
            event.reply("You do not have permission to use `/sendhome`.").setEphemeral(true).queue();
            return;
        }

        // ---- required option: name ----
        var nameOpt = event.getOption("name");
        if (nameOpt == null || nameOpt.getAsString().isBlank()) {
            event.reply("Usage: `/sendhome name:<player>`").setEphemeral(true).queue();
            return;
        }
        final String name = nameOpt.getAsString().trim();

        // ---- find player & act ----
        Player target = PlayerHandler.getPlayerByDisplayName(name);
        if (target == null) {
            event.reply("`" + name + "` must be online to send them home.").setEphemeral(true).queue();
            return;
        }

        // Teleport home (coords from your snippet)
        try {
            target.getPA().spellTeleport(3087, 3500, 0, true);
            event.reply("Yes, master. **" + target.getDisplayName() + "** has been sent home.").setEphemeral(true).queue();

            // Log to server logs channel
            member.getUser();
            String staffName = member.getUser().getName();
            Discord.writeServerSyncMessage("[SendHome] %s sent %s home.", staffName, target.getDisplayName());
        } catch (Throwable t) {
            event.reply("Failed to send **" + target.getDisplayName() + "** home.").setEphemeral(true).queue();
            member.getUser();
            String staffName = member.getUser().getName();
            Discord.writeServerSyncMessage("[SendHome-Error] %s tried to send %s home: %s",
                    staffName, name, String.valueOf(t.getMessage()));
        }
    }

    // ----- helpers -----
    private boolean hasRequiredRole(Member member, String[] roleIds) {
        if (member == null || roleIds == null || roleIds.length == 0) return false;
        for (Role r : member.getRoles()) {
            String id = r.getId();
            for (String need : roleIds) {
                if (id.equals(need)) return true;
            }
        }
        return false;
    }
}
