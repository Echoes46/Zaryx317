package io.zaryx.content.commands.all;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.discord.DiscordBot;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.Optional;

public class Bug extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (input == null) {
			c.sendMessage("Please redo your message.");
			return;
		}
		if (DiscordBot.INSTANCE != null) {
			EmbedBuilder embed = new EmbedBuilder();
			embed.setTitle("[ HELP CHAT ]");
			embed.setColor(Color.RED);
			embed.setTimestamp(java.time.Instant.now());
			embed.addField(c.getDisplayName() + ": " + input, "\u200B", false);
			DiscordBot.INSTANCE.writeHelpChat(embed.build());
		}
		c.sendMessage("Your bug report has been sent to the staff.");
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Report a bug");
	}
}
