package io.zaryx.util.discord.impl;

import io.zaryx.model.definitions.ItemDef;
import io.zaryx.model.items.GameItem;
import io.zaryx.util.Misc;
import io.zaryx.util.discord.Discord;
import io.zaryx.util.offlinestorage.ItemCollection;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OfflineReward extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        User user = e.getMessage().getAuthor();
        String[] params = e.getMessage().getContentRaw().toLowerCase().split("-");
        if (params == null || params.length != 4) {
            user.openPrivateChannel().queue((channel) -> channel.sendMessage("Invalid entry").queue());
            return;
        }
        String name = params[1].toLowerCase();
        int id = Integer.parseInt(params[2]);
        int amount = Integer.parseInt(params[3]);

        ItemCollection.add(name, new GameItem(id, amount));

//        Discord.writeOfflineRewardsMessage("```[OFFLINE REWARDS] " + user.getName() + " gave " + Misc.capitalizeJustFirst(name) + " Item: " + ItemDef.forId(id).getName() + " x " + amount + " (" + id +")```");
    }

}
