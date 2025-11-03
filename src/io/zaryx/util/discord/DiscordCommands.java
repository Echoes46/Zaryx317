package io.zaryx.util.discord;

import io.zaryx.util.discord.impl.*;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
/** Updated by Khaos */
public enum DiscordCommands {

    GIVE_ITEM(
            "giveitem",
            "Give an item to a player",
            new GiveItem(),
            roles(Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE)
    ),

    DELETE_TP(
            "tpdelete",
            "Removes a player's Trading post listing",
            new TpDelete(),
            roles(Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE)
    ),

    BAN(
            "ban",
            "Ban a player",
            new Ban(),
            roles(Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE)
    ),

    UNBAN(
            "unban",
            "Unban a player",
            new UnBan(),
            roles(Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE)
    ),

    MUTE(
            "mute",
            "Mute a player",
            new Mute(),
            roles(Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE, Discord.SUPPORT_ROLE)
    ),

    JAIL(
            "jail",
            "Jail a player",
            new Jail(),
            roles(Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE, Discord.SUPPORT_ROLE)
    ),

    UNMUTE(
            "unmute",
            "Unmute a player",
            new UnMute(),
            roles(Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE)
    ),

    UNJAIL(
            "unjail",
            "Unjail a player",
            new UnJail(),
            roles(Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE)
    ),

    XMAS(
            "xmas",
            "Xmas check for player",
            new xmas(),
            roles(Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE)
    ),

    GROOT(
            "groot",
            "Spawns Groot",
            new Groot(),
            roles(Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE)
    ),

    VOTEBOSS(
            "voteboss",
            "Spawns vote boss",
            new VoteBoss(),
            roles(Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE, Discord.SUPPORT_ROLE)
    ),

    DONORBOSS(
            "donorboss",
            "Spawns donor boss",
            new DonorBoss(),
            roles(Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE)
    ),

    OFFLINEREWARDS(
            "of",
            "Gives offline reward",
            new OfflineReward(),
            roles(Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE)
    ),

    COMMANDS(
            "commands",
            "Shows all commands",
            new Commands(),
            roles(Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE, Discord.SUPPORT_ROLE)
    );

    private final String command;
    private final String description;
    private final ListenerAdapter adapter;
    private final Set<String> rolesCanUse; // role IDs as strings

    public static final DiscordCommands[] VALUES = DiscordCommands.values();
    public static String prefix = Discord.PREFIX;

    DiscordCommands(String command, String description, ListenerAdapter adapter, Set<String> rolesCanUse) {
        this.command = command;
        this.description = description;
        this.adapter = adapter;
        this.rolesCanUse = rolesCanUse;
    }

    private static Set<String> roles(String... roleIds) {
        // de-duplicate & fast lookup
        return new HashSet<>(Arrays.asList(roleIds));
    }

    /**
     * Attempts to resolve which command this message is invoking.
     * Rules:
     * - must start with "<prefix><command>" either exactly, or followed by whitespace
     * - author must have at least one of the required roles (if any)
     */
    public static DiscordCommands isCommand(GuildMessageReceivedEvent e) {
        final String raw = e.getMessage().getContentRaw();
        if (raw == null) return null;

        final String text = raw.trim().toLowerCase();
        final Member member = e.getMember();
        if (member == null) return null; // webhooks or unknown member

        for (DiscordCommands cmd : VALUES) {
            final String needle = (prefix + cmd.command).toLowerCase();

            if (text.equals(needle) || text.startsWith(needle + " ") || text.startsWith(needle + "\n")) {
                // role check (if roles list is empty, treat as allowed)
                if (cmd.rolesCanUse == null || cmd.rolesCanUse.isEmpty() || hasAnyRole(member, cmd.rolesCanUse)) {
                    return cmd;
                }
            }
        }
        return null;
    }

    private static boolean hasAnyRole(Member member, Set<String> allowedRoleIds) {
        if (allowedRoleIds == null || allowedRoleIds.isEmpty()) return true;
        for (Role r : member.getRoles()) {
            // Use equals on the role ID, never "contains"
            if (allowedRoleIds.contains(r.getId())) {
                return true;
            }
        }
        return false;
    }
}
