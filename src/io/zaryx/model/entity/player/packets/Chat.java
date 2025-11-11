package io.zaryx.model.entity.player.packets;

import io.zaryx.Server;
import io.zaryx.content.Censor;
import io.zaryx.model.entity.player.PacketType;
import io.zaryx.model.entity.player.Player;
import io.zaryx.punishments.PunishmentType;
import io.zaryx.util.Misc;
import io.zaryx.util.discord.DiscordBot;
import io.zaryx.util.discord.DiscordChannelType;
import io.zaryx.util.logging.player.PublicChatLog;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

/**
 * Chat
 **/
public class Chat implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		byte[] chatText = new byte[4096];
		c.setChatTextEffects(c.getInStream().readUnsignedByteS());
		c.setChatTextColor(c.getInStream().readUnsignedByteS());
		c.setChatTextSize((byte) (packetSize - 2));

		c.inStream.readBytes_reverseA(chatText, c.getChatTextSize(), 0);

		if (c.getBankPin().requiresUnlock()) {
            c.getBankPin().open(2);
            c.sendMessage("You are muted until you unlock your bank pin.");
            return;
        }
		if (Server.getPunishments().isNetMuted(c)) {
			c.sendMessage("Your entire network has been muted. Other players cannot see your message.");
			return;
		}

		if (Server.getPunishments().contains(PunishmentType.MUTE, c.getLoginName())) {
			c.sendMessage("You are currently muted. Other players cannot see your message.");
			return;
		}

		if (System.currentTimeMillis() < c.muteEnd) {
			c.sendMessage("You are currently muted. Other players cannot see your message.");
			return;
		}

		String message = Misc.decodeMessage(chatText, c.getChatTextSize());

		if (message == null || !Misc.isValidChatMessage(message)) {
			c.sendMessage("Invalid chat message.");
			return;
		}

		for (byte b : chatText) {
			if (b >= Misc.validChars.length) {
				c.sendMessage("Invalid chat message.");
				return;
			}
		}

		if (Misc.isSpam(message)) {
			c.sendMessage("Please don't spam.");
			return;
		}

		Server.getLogging().write(new PublicChatLog(c, message));

		if (DiscordBot.INSTANCE != null) {
			EmbedBuilder embed = new EmbedBuilder();
			embed.setTitle("Public Chat Message Test");
			embed.setColor(Color.GRAY);
			embed.setTimestamp(java.time.Instant.now());
			embed.addField("Player", c.getDisplayName(), false);
			embed.addField("Location", c.getLocation().toString(), false);
			embed.addField("Message", message.length() > 1000 ? message.substring(0, 1000) + "..." : message, false);
			DiscordBot.INSTANCE.sendChatLogs(embed.build());
		}

		if (Censor.isCensored(c, message)) {
			return;
		}

		c.setChatTextUpdateRequired(true);
		c.setChatText(chatText);
	}

}
