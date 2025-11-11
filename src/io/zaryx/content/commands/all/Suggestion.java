package io.zaryx.content.commands.all;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.discord.DiscordBot;
import io.zaryx.util.discord.DiscordChannelType;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.Optional;

public class Suggestion extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (input == null) {
			c.sendMessage("Please redo your message.");
			return;
		}
		//Discord.writeSuggestionMessage(c.getDisplayName() + ": " + input);
		if (DiscordBot.INSTANCE != null) {
			EmbedBuilder embed = new EmbedBuilder();
			embed.setTitle(" PLAYER SUGGESTION ");
			embed.setColor(Color.RED);
			embed.setTimestamp(java.time.Instant.now());
			embed.addField("Player: ", c.getDisplayName() + " has sent a suggestion.", false);
			DiscordBot.INSTANCE.sendStaffLogs(embed.build());
		}
		c.sendMessage("Your suggestion has been sent to the staff.");
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Suggestion that we add or change something");
	}
}
