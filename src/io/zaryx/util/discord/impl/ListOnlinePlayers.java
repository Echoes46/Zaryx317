package io.zaryx.util.discord.impl;

import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.util.discord.Discord;
import io.zaryx.util.discord.SlashHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * /listonlineplayers
 * Shows a list of currently online players.
 */
public class ListOnlinePlayers implements SlashHandler {

    // ===== central-router entry point =====
    @Override
    public void handle(SlashCommandInteractionEvent event) {
        if (!"listonlineplayers".equals(event.getName())) return;

        // --- Permission gate (adjust as you like) ---
        // Allowed: support and above
        String[] requiredRoleIds = {
                Discord.SUPPORT_ROLE,
                Discord.GLOBAL_MOD_ROLE,
                Discord.ADMIN_ROLE,
                Discord.MANAGER_ROLE,
                Discord.DEVELOPER_ROLE,
                Discord.OWNER_ROLE,
                Discord.MEMBER_ROLE
        };
        if (!hasAnyRequiredRole(event.getMember(), requiredRoleIds)) {
            event.reply("You do not have permission to use `/listonlineplayers`.").setEphemeral(true).queue();
            return;
        }

        // --- Collect online players ---
        List<String> names = getOnlinePlayerNames();

        if (names.isEmpty()) {
            event.reply("There are no players online.").setEphemeral(true).queue();
            return;
        }

        // Build a clean embed. Avoid per-field spam; put names in a single block.
        String list = String.join("\n", names);
        // JDA limit safety: trim if too long
        if (list.length() > 3800) {
            list = list.substring(0, 3800) + "\n…";
        }

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Online Players")
                .setColor(new Color(0x00FF00))
                .setDescription("Total: **" + names.size() + "**\n```" + list + "```");

        event.replyEmbeds(eb.build()).setEphemeral(true).queue();
    }

    // ===== helpers =====

    private boolean hasAnyRequiredRole(Member member, String[] roleIds) {
        if (member == null || roleIds == null || roleIds.length == 0) return false;
        var memberRoles = member.getRoles();
        for (String id : roleIds) {
            for (var r : memberRoles) {
                if (r.getId().equals(id)) return true;
            }
        }
        return false;
    }

    private List<String> getOnlinePlayerNames() {
        List<String> names = new ArrayList<>();

        // Common layout: PlayerHandler.players is an array
        try {
            Player[] arr = PlayerHandler.players;
            if (arr != null) {
                names.addAll(Arrays.stream(arr)
                        .filter(p -> p != null && p.isOnline())
                        .map(p -> {
                            // Prefer display name if available
                            try {
                                String dn = p.getDisplayName();
                                return (dn != null && !dn.isBlank()) ? dn : p.getLoginNameLower();
                            } catch (Throwable t) {
                                return "unknown";
                            }
                        })
                        .collect(Collectors.toList()));
            }
        } catch (Throwable ignored) {
            // Fallback if your project exposes a different accessor:
            // if (PlayerHandler.getPlayers() != null) { ... }
        }
        return names;
    }
}
