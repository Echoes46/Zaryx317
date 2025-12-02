package io.zaryx.util.discord.impl;

import io.zaryx.Server;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.entity.player.save.PlayerAddresses;
import io.zaryx.punishments.PunishmentType;
import io.zaryx.util.discord.Discord;
import io.zaryx.util.discord.SlashHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class NetUnMute implements SlashHandler {

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        if (!"netunmute".equals(event.getName())) return;

        // ---- permissions (owner/dev/manager/admin) ----
        Member member = event.getMember();
        String[] allowedRoles = {
                Discord.OWNER_ROLE,
                Discord.DEVELOPER_ROLE,
                Discord.MANAGER_ROLE,
                Discord.ADMIN_ROLE
        };
        if (!hasRequiredRole(member, allowedRoles)) {
            event.reply("You do not have permission to use `/netunmute`.").setEphemeral(true).queue();
            return;
        }

        // ---- required option: name ----
        var nameOpt = event.getOption("name");
        if (nameOpt == null || nameOpt.getAsString().isBlank()) {
            event.reply("Usage: `/netunmute name:<player>`").setEphemeral(true).queue();
            return;
        }
        final String name = nameOpt.getAsString().trim();

        // ---- find player (must be online to read identifiers) ----
        Player p = PlayerHandler.getPlayerByDisplayName(name);
        if (p == null) {
            event.reply("`" + name + "` is not online or doesn't exist.").setEphemeral(true).queue();
            return;
        }

        // ---- clear local mute flags ----
        try {
            p.setHelpCcMuted(false);
        } catch (Throwable ignored) {}
        try {
            p.muteEnd = 0;
        } catch (Throwable ignored) {}

        // ---- remove network mutes by identifiers ----
        int removed = 0;
        PlayerAddresses addresses = p.getValidAddresses();
        if (addresses != null) {
            if (addresses.getIp() != null) {
                Server.getPunishments().remove(PunishmentType.NET_MUTE, addresses.getIp());
                removed++;
            }
            if (addresses.getMac() != null) {
                Server.getPunishments().remove(PunishmentType.NET_MUTE, addresses.getMac());
                removed++;
            }
            if (addresses.getUUID() != null && !addresses.getUUID().isEmpty()) {
                Server.getPunishments().remove(PunishmentType.NET_MUTE, addresses.getUUID());
                removed++;
            }
        }

        member.getUser();
        String staffName = member.getUser().getName();
        p.sendMessage("You have been unmuted by " + staffName + ".");
        event.reply("Unmuted **" + p.getDisplayName() + "** (identifiers cleared: " + removed + ").")
                .setEphemeral(true).queue();

        // Log (use an existing helper; there is no writepunishments in your Discord class)
        Discord.writePunishmentLog("[NetUnmute] %s unmuted %s (identifiers cleared: %d)",
                staffName, p.getDisplayName(), removed);
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
