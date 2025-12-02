package io.zaryx.util.discord.impl;

import io.zaryx.model.definitions.ItemDef;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.util.Misc;
import io.zaryx.util.discord.Discord;
import io.zaryx.util.discord.SlashHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

/**
 * Slash command: /giveitem name:<string> id:<int> amount:<int>
 * Gives an item to an online player and logs to the give-log channel.
 */
public class GiveItem extends ListenerAdapter implements SlashHandler {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (!"giveitem".equals(e.getName())) {
            return;
        }

        OptionMapping nameOpt   = e.getOption("name");
        OptionMapping idOpt     = e.getOption("id");
        OptionMapping amountOpt = e.getOption("amount");

        if (nameOpt == null || idOpt == null || amountOpt == null) {
            e.reply("Usage: `/giveitem name:<player> id:<itemId> amount:<count>`").setEphemeral(true).queue();
            return;
        }

        final String name = nameOpt.getAsString().trim();
        final int id = idOpt.getAsInt();
        final int amount = amountOpt.getAsInt();

        if (amount <= 0) {
            e.reply("Amount must be a positive integer.").setEphemeral(true).queue();
            return;
        }

        // Validate item def if available
        ItemDef def = ItemDef.forId(id);
        if (def == null) {
            e.reply("Unknown item id: `" + id + "`.").setEphemeral(true).queue();
            return;
        }

        Player target = PlayerHandler.getPlayerByDisplayName(name);
        if (target == null) {
            e.reply("`" + name + "` must be online to receive items.").setEphemeral(true).queue();
            return;
        }

        // Give item
        target.getItems().addItemUnderAnyCircumstance(id, amount);

        // Notify target
        target.sendMessage(e.getUser().getName() + " gave you " + def.getName() + " x " + amount);

        // Log to Discord
        String prettyName = Misc.capitalizeJustFirst(name);
        Discord.writeGiveLog("[Give-Log] " + e.getUser().getName() + " gave " + prettyName
                + " Item: " + def.getName() + " x " + amount + " (" + id + ")");

        // Acknowledge to command user
        e.reply("Gave **" + amount + "x " + def.getName() + "** to **" + prettyName + "**.").queue();
    }
    @Override
    public void handle(SlashCommandInteractionEvent e) {
        onSlashCommandInteraction(e);
    }
}
