package io.zaryx.util.discord.impl;

import io.zaryx.util.discord.Discord;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Groot extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        User user = e.getMessage().getAuthor();
        io.zaryx.content.activityboss.Groot.spawnGroot();
        Discord.writeGiveLog("[Groot] " + user.getName() + " has spawned groot!");
    }

}