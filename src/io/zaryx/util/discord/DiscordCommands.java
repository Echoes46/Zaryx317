package io.zaryx.util.discord;

import io.zaryx.util.discord.impl.*;
import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Getter
public enum DiscordCommands {

    GIVE_ITEM("giveitem", "Give an item to a player", new GiveItem(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE}),
   // DELETE_TP("tpdelete", "Removes a player's Trading Post listing", new TpDelete(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE}),
    BAN("ban", "Ban a player", new Ban(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE}),
    UNBAN("unban", "Unban a player", new UnBan(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE}),
    MUTE("mute", "Mute a player", new Mute(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE, Discord.SUPPORT_ROLE}),
    JAIL("jail", "Jail a player", new Jail(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE, Discord.SUPPORT_ROLE}),
    UNMUTE("unmute", "Unmute a player", new UnMute(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE}),
    UNJAIL("unjail", "Unjail a player", new UnJail(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE}),
    XMAS("xmas", "Check Xmas quest info", new xmas(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE}),
    GROOT("groot", "Spawn Groot", new Groot(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE}),
    VOTEBOSS("voteboss", "Spawn Vote Boss", new VoteBoss(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE, Discord.SUPPORT_ROLE}),
    DONORBOSS("donorboss", "Spawn Donor Boss", new DonorBoss(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE}),
    OFFLINEREWARDS("offlinereward", "Give offline reward", new OfflineReward(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE}),
    COMMANDS("commands", "Show all commands", new Commands(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE, Discord.SUPPORT_ROLE});

    private final String command;
    private final String description;
    private final ListenerAdapter adapter;
    private final String[] rolesCanUse;

    DiscordCommands(String command, String description, ListenerAdapter adapter, String[] rolesCanUse) {
        this.command = command;
        this.description = description;
        this.adapter = adapter;
        this.rolesCanUse = rolesCanUse;
    }

    /** Check if user can use this command based on their roles */
    public boolean canUse(SlashCommandInteractionEvent e) {
        if (rolesCanUse == null || rolesCanUse.length == 0) return true;
        return e.getMember() != null && e.getMember().getRoles().stream()
                .anyMatch(r -> java.util.Arrays.asList(rolesCanUse).contains(r.getId()));
    }

    /** Find a command by name */
    public static DiscordCommands fromName(String name) {
        for (DiscordCommands c : values()) {
            if (c.getCommand().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }
}
