package io.zaryx.content.commands.helper;

import io.zaryx.Server;
import io.zaryx.content.commands.Command;
import io.zaryx.model.cycleevent.CycleEventHandler;
import io.zaryx.model.entity.player.ConnectedFrom;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.util.discord.DiscordBot;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.Optional;

/**
 * Forces a given player to log out.
 * 
 * @author Emiel
 */
public class Kick extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(input);
		if (optionalPlayer.isPresent()) {
			Player c2 = optionalPlayer.get();
			if (Server.getMultiplayerSessionListener().inAnySession(c)) {
				c.sendMessage("The player is in a trade, or duel. You cannot do this at this time.");
				return;
			}

			c2.outStream.createFrame(109);
			CycleEventHandler.getSingleton().stopEvents(c2);
			if (DiscordBot.INSTANCE != null) {
				EmbedBuilder embed = new EmbedBuilder();
				embed.setTitle(" PLAYER KICKING ");
				embed.setColor(Color.RED);
				embed.setTimestamp(java.time.Instant.now());
				embed.addField("Player: ", c.getDisplayName() + " Kicked " + c2.getDisplayName(), false);
				DiscordBot.INSTANCE.sendStaffLogs(embed.build());
			}
			c2.forceLogout();
			ConnectedFrom.addConnectedFrom(c2, c2.connectedFrom);
			c.sendMessage("Kicked " + c2.getDisplayName());
		} else {
			c.sendMessage(input + " is not online. You can only kick online players.");
		}
	}
}
