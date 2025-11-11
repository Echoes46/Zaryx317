package io.zaryx.content.cheatprevention;

import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.ConnectedFrom;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.Misc;
import io.zaryx.util.discord.DiscordBot;
import io.zaryx.util.discord.DiscordChannelType;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class CheatEngineBlock {

	public static boolean tradingPostAlert(Player c) {
		if ((!Boundary.isIn(c, Boundary.EDGE_TRADING_AREA) && !Boundary.isIn(c, Boundary.SKILLING_ISLAND_BANK)) || Boundary.isIn(c, Boundary.OUTLAST_HUT)) {
			Misc.println("```" + c.getDisplayName() + " is trying to use a cheatengine to open the Trading post.```");
			Misc.println("```" + c.getDisplayName() + " is trying to use a cheatengine to open the Trading post.```");
			Misc.println("```" + c.getDisplayName() + " is trying to use a cheatengine to open the Trading post.```");
			Misc.println("```" + c.getDisplayName() + " is trying to use a cheatengine to open the Trading post.```");
			if (DiscordBot.INSTANCE != null) {
				EmbedBuilder embed = new EmbedBuilder();
				embed.setTitle(" [ TRADING POST ABUSE ALERT ] ");
				embed.setColor(Color.BLUE);
				embed.setTimestamp(java.time.Instant.now());
				embed.addField("Player: ", c.getDisplayName() + " is using a cheat engine for the trading post!", false);
				DiscordBot.INSTANCE.sendStaffLogs(embed.build());
				DiscordBot.INSTANCE.sendMessage(DiscordChannelType.STAFF_LOGS, "@everyone");
			}
			c.getPA().closeAllWindows();
			return true;
		} else {
			if (DiscordBot.INSTANCE != null) {
				EmbedBuilder embed = new EmbedBuilder();
				embed.setTitle(" [ TRADING POST ABUSE ALERT ] ");
				embed.setColor(Color.BLUE);
				embed.setTimestamp(java.time.Instant.now());
				embed.addField("Player: ", c.getDisplayName() + " triggered trading post in edge but no jail.", false);
				DiscordBot.INSTANCE.sendStaffLogs(embed.build());
				DiscordBot.INSTANCE.sendMessage(DiscordChannelType.STAFF_LOGS, "@everyone");
			}
			return false;
		}
	}


		public static boolean BankAlert(Player c) {
			if ((!Boundary.isIn(c, Boundary.EDGE_TRADING_AREA) && !Boundary.isIn(c, Boundary.SKILLING_ISLAND_BANK)) || Boundary.isIn(c, Boundary.OUTLAST_HUT)) {
				Misc.println("```" + c.getDisplayName() + " is trying to use a cheatengine to open the Bank.```");
				Misc.println("```" + c.getDisplayName() + " is trying to use a cheatengine to open the Bank.```");
				Misc.println("```" + c.getDisplayName() + " is trying to use a cheatengine to open the Bank.```");
				Misc.println("```" + c.getDisplayName() + " is trying to use a cheatengine to open the Bank.```");
				if (DiscordBot.INSTANCE != null) {
					EmbedBuilder embed = new EmbedBuilder();
					embed.setTitle(" [ BANK ABUSE ALERT ] ");
					embed.setColor(Color.BLUE);
					embed.setTimestamp(java.time.Instant.now());
					embed.addField("Player: ", c.getDisplayName() + " is using a cheat engine for the Bank!", false);
					DiscordBot.INSTANCE.sendStaffLogs(embed.build());
					DiscordBot.INSTANCE.sendMessage(DiscordChannelType.STAFF_LOGS, "@everyone");
				}
				c.setTeleportToX(2086);
				c.setTeleportToY(4466);
				c.heightLevel = 0;
				c.getPA().closeAllWindows();
				return true;
			} else {
				if (DiscordBot.INSTANCE != null) {
					EmbedBuilder embed = new EmbedBuilder();
					embed.setTitle(" [ BANK ABUSE ALERT ] ");
					embed.setColor(Color.BLUE);
					embed.setTimestamp(java.time.Instant.now());
					embed.addField("Player: ", c.getDisplayName() + " triggered trading post in edge but no jail.", false);
					DiscordBot.INSTANCE.sendStaffLogs(embed.build());
					DiscordBot.INSTANCE.sendMessage(DiscordChannelType.STAFF_LOGS, "@everyone");
				}
				c.setTeleportToX(2086);
				c.setTeleportToY(4466);
				c.heightLevel = 0;
				c.forceLogout();
				ConnectedFrom.addConnectedFrom(c, c.connectedFrom);
				if (DiscordBot.INSTANCE != null) {
					EmbedBuilder embed = new EmbedBuilder();
					embed.setTitle(" [ BANK ABUSE ALERT ] ");
					embed.setColor(Color.BLUE);
					embed.setTimestamp(java.time.Instant.now());
					embed.addField("I Kicked " + c.getDisplayName(), "\u200B", false);
					DiscordBot.INSTANCE.sendStaffLogs(embed.build());
					DiscordBot.INSTANCE.sendMessage(DiscordChannelType.STAFF_LOGS, "@everyone");
				}
				c.getPA().closeAllWindows();
				return false;
			}
			}

		public static boolean PresetAlert(Player c) {
			if ((!Boundary.isIn(c, Boundary.EDGE_TRADING_AREA) && !Boundary.isIn(c, Boundary.SKILLING_ISLAND_BANK)) || Boundary.isIn(c, Boundary.OUTLAST_HUT)) {
				Misc.println("```" + c.getDisplayName() + " is trying to use a cheatengine to open the Presets.```");
				Misc.println("```" + c.getDisplayName() + " is trying to use a cheatengine to open the Presets.```");
				Misc.println("```" + c.getDisplayName() + " is trying to use a cheatengine to open the Presets.```");
				Misc.println("```" + c.getDisplayName() + " is trying to use a cheatengine to open the Presets.```");
				if (DiscordBot.INSTANCE != null) {
					EmbedBuilder embed = new EmbedBuilder();
					embed.setTitle(" [ PRESET ABUSE ] ");
					embed.setColor(Color.BLUE);
					embed.setTimestamp(java.time.Instant.now());
					embed.addField("Player: ", c.getDisplayName() + " is using a cheat engine for the Presets!", false);
					DiscordBot.INSTANCE.sendStaffLogs(embed.build());
					DiscordBot.INSTANCE.sendMessage(DiscordChannelType.STAFF_LOGS, "@everyone");
				}
				c.setTeleportToX(2086);
				c.setTeleportToY(4466);
				c.heightLevel = 0;
				c.getPA().closeAllWindows();
				return true;
			} else {
				if (DiscordBot.INSTANCE != null) {
					EmbedBuilder embed = new EmbedBuilder();
					embed.setTitle(" [ PRESET ABUSE ] ");
					embed.setColor(Color.BLUE);
					embed.setTimestamp(java.time.Instant.now());
					embed.addField("Player: ", c.getDisplayName() + " triggered trading post in edge but no jail.", false);
					DiscordBot.INSTANCE.sendStaffLogs(embed.build());
					DiscordBot.INSTANCE.sendMessage(DiscordChannelType.STAFF_LOGS, "@everyone");
				}
				c.setTeleportToX(2086);
				c.setTeleportToY(4466);
				c.heightLevel = 0;
				c.forceLogout();
				ConnectedFrom.addConnectedFrom(c, c.connectedFrom);
				if (DiscordBot.INSTANCE != null) {
					EmbedBuilder embed = new EmbedBuilder();
					embed.setTitle(" [ PRESET ABUSE ] ");
					embed.setColor(Color.BLUE);
					embed.setTimestamp(java.time.Instant.now());
					embed.addField("I Kicked " + c.getDisplayName(), "\u200B", false);
					DiscordBot.INSTANCE.sendStaffLogs(embed.build());
					DiscordBot.INSTANCE.sendMessage(DiscordChannelType.STAFF_LOGS, "@everyone");
				}
				c.getPA().closeAllWindows();
				return false;
			}
		}
		public static boolean DonatorBoxAlert(Player c) {
			Misc.println("```"+ c.getDisplayName() +" is trying to use a cheatengine to open the donator boxes.```");
			Misc.println("```"+ c.getDisplayName() +" is trying to use a cheatengine to open the donator boxes.```");
			Misc.println("```"+ c.getDisplayName() +" is trying to use a cheatengine to open the donator boxes.```");
			Misc.println("```"+ c.getDisplayName() +" is trying to use a cheatengine to open the donator boxes.```");
			if (DiscordBot.INSTANCE != null) {
				EmbedBuilder embed = new EmbedBuilder();
				embed.setTitle(" [ DONATOR BOX ABUSE ] ");
				embed.setColor(Color.BLUE);
				embed.setTimestamp(java.time.Instant.now());
				embed.addField("Player: ", c.getDisplayName() + " is using a cheat engine for the donator boxes!", false);
				DiscordBot.INSTANCE.sendStaffLogs(embed.build());
				DiscordBot.INSTANCE.sendMessage(DiscordChannelType.STAFF_LOGS, "@everyone");
			}
			c.setTeleportToX(2086);
			c.setTeleportToY(4466);
			c.heightLevel = 0;
			c.forceLogout();
			ConnectedFrom.addConnectedFrom(c, c.connectedFrom);
			if (DiscordBot.INSTANCE != null) {
				EmbedBuilder embed = new EmbedBuilder();
				embed.setTitle(" [ DONATOR BOX ABUSE ] ");
				embed.setColor(Color.BLUE);
				embed.setTimestamp(java.time.Instant.now());
				embed.addField("I Kicked " + c.getDisplayName(), "\u200B", false);
				DiscordBot.INSTANCE.sendStaffLogs(embed.build());
				DiscordBot.INSTANCE.sendMessage(DiscordChannelType.STAFF_LOGS, "@everyone");
			}
			c.getPA().closeAllWindows();
			return true;
		}
		public static boolean ExperienceAbuseAlert(Player c) {
			Misc.println("```"+ c.getDisplayName() +" is trying to use a cheatengine to open the lamps.```");
			Misc.println("```"+ c.getDisplayName() +" is trying to use a cheatengine to open the lamps.```");
			Misc.println("```"+ c.getDisplayName() +" is trying to use a cheatengine to open the lamps.```");
			Misc.println("```"+ c.getDisplayName() +" is trying to use a cheatengine to open the lamps.```");
			if (DiscordBot.INSTANCE != null) {
				EmbedBuilder embed = new EmbedBuilder();
				embed.setTitle(" [ EXPERIENCE ABUSE ] ");
				embed.setColor(Color.BLUE);
				embed.setTimestamp(java.time.Instant.now());
				embed.addField("Player: ", c.getDisplayName() + " is using a cheat engine for the lamps!", false);
				DiscordBot.INSTANCE.sendStaffLogs(embed.build());
				DiscordBot.INSTANCE.sendMessage(DiscordChannelType.STAFF_LOGS, "@everyone");
			}
			c.setTeleportToX(2086);
			c.setTeleportToY(4466);
			c.forceLogout();
			ConnectedFrom.addConnectedFrom(c, c.connectedFrom);
			if (DiscordBot.INSTANCE != null) {
				EmbedBuilder embed = new EmbedBuilder();
				embed.setTitle(" [ EXPERIENCE ABUSE ] ");
				embed.setColor(Color.BLUE);
				embed.setTimestamp(java.time.Instant.now());
				embed.addField("I Kicked " + c.getDisplayName(), "\u200B", false);
				DiscordBot.INSTANCE.sendStaffLogs(embed.build());
				DiscordBot.INSTANCE.sendMessage(DiscordChannelType.STAFF_LOGS, "@everyone");
			}
			c.heightLevel = 0;
			c.getPA().closeAllWindows();
			return true;
		}

}