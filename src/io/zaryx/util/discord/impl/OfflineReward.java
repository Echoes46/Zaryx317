package io.zaryx.util.discord.impl;

import io.zaryx.model.definitions.ItemDef;
import io.zaryx.model.items.GameItem;
import io.zaryx.util.Misc;
import io.zaryx.util.discord.Discord;
import io.zaryx.util.discord.SlashHandler;
import io.zaryx.util.offlinestorage.ItemCollection;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

/**
 * Slash command: /offlinereward name:<string> id:<int> amount:<int>
 * Queues an item reward for a player who might be offline and logs to Discord.
 */
public class OfflineReward extends ListenerAdapter implements SlashHandler{

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (!"offlinereward".equals(e.getName())) {
            return;
        }

        OptionMapping nameOpt   = e.getOption("name");
        OptionMapping idOpt     = e.getOption("id");
        OptionMapping amountOpt = e.getOption("amount");

        if (nameOpt == null || idOpt == null || amountOpt == null) {
            e.reply("Usage: `/offlinereward name:<player> id:<itemId> amount:<count>`")
                    .setEphemeral(true).queue();
            return;
        }

        final String name = nameOpt.getAsString().trim().toLowerCase();
        final int id = idOpt.getAsInt();
        final int amount = amountOpt.getAsInt();

        if (amount <= 0) {
            e.reply("Amount must be a positive integer.").setEphemeral(true).queue();
            return;
        }

        // Optional: validate item id
        ItemDef def = ItemDef.forId(id);
        if (def == null) {
            e.reply("Unknown item id: `" + id + "`.").setEphemeral(true).queue();
            return;
        }

        // Queue reward for offline storage
        ItemCollection.add(name, new GameItem(id, amount));

        // Log to your offline rewards channel
        Discord.writeOfflineRewardsMessage("[OFFLINE REWARDS] " + e.getUser().getName()
                + " gave " + Misc.capitalizeJustFirst(name) + " Item: " + def.getName()
                + " x " + amount + " (" + id + ")");
        // Acknowledge to the invoker
        e.reply("Queued **" + amount + "x " + def.getName() + "** for **" + Misc.capitalizeJustFirst(name) + "**.").queue();
    }
    @Override
    public void handle(SlashCommandInteractionEvent e) {
        onSlashCommandInteraction(e);
    }
}
