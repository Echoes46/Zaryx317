package io.zaryx.content.cheatprevention;

import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.ConnectedFrom;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.Misc;
import io.zaryx.util.discord.Discord;

public class CheatEngineBlock {

	public static boolean tradingPostAlert(Player c) {
		if ((!Boundary.isIn(c, Boundary.EDGE_TRADING_AREA) && !Boundary.isIn(c, Boundary.SKILLING_ISLAND_BANK)) || Boundary.isIn(c, Boundary.OUTLAST_HUT)) {
			Misc.println("```" + c.getDisplayName() + " is trying to use a cheatengine to open the Trading post.```");
			Misc.println("```" + c.getDisplayName() + " is trying to use a cheatengine to open the Trading post.```");
			Misc.println("```" + c.getDisplayName() + " is trying to use a cheatengine to open the Trading post.```");
			Misc.println("```" + c.getDisplayName() + " is trying to use a cheatengine to open the Trading post.```");
			Discord.writeServerSyncMessage("```[CHEAT ENGINE] " + c.getDisplayName() + " is using a cheat engine for the @red@trading post!```");
			Discord.writeCheatEngineMessage("```[CHEAT ENGINE] " + c.getDisplayName() + " is using a cheat engine for the @red@trading post!```");
			c.getPA().closeAllWindows();
			return true;
		} else {
			Discord.writeServerSyncMessage("```[CHEAT ENGINE] " + c.getDisplayName() + " triggered trading post in edge but no jail.```");
			Discord.writeCheatEngineMessage("```[CHEAT ENGINE] " + c.getDisplayName() + " triggered trading post in edge but no jail.```");
			return false;
		}
	}


		public static boolean BankAlert(Player c) {
			if ((!Boundary.isIn(c, Boundary.EDGE_TRADING_AREA) && !Boundary.isIn(c, Boundary.SKILLING_ISLAND_BANK)) || Boundary.isIn(c, Boundary.OUTLAST_HUT)) {
				Misc.println("```" + c.getDisplayName() + " is trying to use a cheatengine to open the Bank.```");
				Misc.println("```" + c.getDisplayName() + " is trying to use a cheatengine to open the Bank.```");
				Misc.println("```" + c.getDisplayName() + " is trying to use a cheatengine to open the Bank.```");
				Misc.println("```" + c.getDisplayName() + " is trying to use a cheatengine to open the Bank.```");
				Discord.writeServerSyncMessage("[CHEAT ENGINE] " + c.getDisplayName() + " is using a cheat engine for the @red@Bank!```");
				Discord.writeCheatEngineMessage("[CHEAT ENGINE] " + c.getDisplayName() + " is using a cheat engine for the @red@Bank!```");
				c.setTeleportToX(2086);
				c.setTeleportToY(4466);
				c.heightLevel = 0;
				c.getPA().closeAllWindows();
				return true;
			} else {
				Discord.writeServerSyncMessage("```[CHEAT ENGINE] " + c.getDisplayName() + " triggered trading post in edge but no jail.```");
				Discord.writeCheatEngineMessage("```[CHEAT ENGINE] " + c.getDisplayName() + " triggered trading post in edge but no jail.```");
				c.setTeleportToX(2086);
				c.setTeleportToY(4466);
				c.heightLevel = 0;
				c.forceLogout();
				ConnectedFrom.addConnectedFrom(c, c.connectedFrom);
				Discord.writeCheatEngineMessage("```I Kicked " + c.getDisplayName() + "```");
				Discord.writeCheatEngineMessage("```I Kicked " + c.getDisplayName() + "```");
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
				Discord.writeServerSyncMessage("```[CHEAT ENGINE] " + c.getDisplayName() + " @blu@is using a cheat engine for the @red@Presets!```");
				Discord.writeCheatEngineMessage("```[CHEAT ENGINE] " + c.getDisplayName() + " @blu@is using a cheat engine for the @red@Presets!```");
				c.setTeleportToX(2086);
				c.setTeleportToY(4466);
				c.heightLevel = 0;
				c.getPA().closeAllWindows();
				return true;
			} else {
				Discord.writeServerSyncMessage("```[CHEAT ENGINE] " + c.getDisplayName() + " triggered trading post in edge but no jail.```");
				Discord.writeCheatEngineMessage("```[CHEAT ENGINE] " + c.getDisplayName() + " triggered trading post in edge but no jail.```");
				c.setTeleportToX(2086);
				c.setTeleportToY(4466);
				c.heightLevel = 0;
				c.forceLogout();
				ConnectedFrom.addConnectedFrom(c, c.connectedFrom);
				Discord.writeCheatEngineMessage("```I Kicked " + c.getDisplayName() + "```");
				Discord.writeCheatEngineMessage("```I Kicked " + c.getDisplayName() + "```");
				c.getPA().closeAllWindows();
				return false;
			}
		}
		public static boolean DonatorBoxAlert(Player c) {
			Misc.println("```"+ c.getDisplayName() +" is trying to use a cheatengine to open the donator boxes.```");
			Misc.println("```"+ c.getDisplayName() +" is trying to use a cheatengine to open the donator boxes.```");
			Misc.println("```"+ c.getDisplayName() +" is trying to use a cheatengine to open the donator boxes.```");
			Misc.println("```"+ c.getDisplayName() +" is trying to use a cheatengine to open the donator boxes.```");
			Discord.writeServerSyncMessage("```[CHEAT ENGINE] "+ c.getDisplayName() +" is using a cheat engine for the @red@donator boxes!```");
			Discord.writeCheatEngineMessage("```[CHEAT ENGINE] " + c.getDisplayName() + " is using a cheat engine for the @red@donator boxes!```");
			c.setTeleportToX(2086);
			c.setTeleportToY(4466);
			c.heightLevel = 0;
			c.forceLogout();
			ConnectedFrom.addConnectedFrom(c, c.connectedFrom);
			Discord.writeCheatEngineMessage("```I Kicked " + c.getDisplayName() + "```");
			Discord.writeCheatEngineMessage("```I Kicked " + c.getDisplayName() + "```");
			c.getPA().closeAllWindows();
			return true;
		}
		public static boolean ExperienceAbuseAlert(Player c) {
			Misc.println("```"+ c.getDisplayName() +" is trying to use a cheatengine to open the lamps.```");
			Misc.println("```"+ c.getDisplayName() +" is trying to use a cheatengine to open the lamps.```");
			Misc.println("```"+ c.getDisplayName() +" is trying to use a cheatengine to open the lamps.```");
			Misc.println("```"+ c.getDisplayName() +" is trying to use a cheatengine to open the lamps.```");

			Discord.writeServerSyncMessage("```[CHEAT ENGINE] "+ c.getDisplayName() +" is using a cheat engine for the lamps!```");
			Discord.writeCheatEngineMessage("```[CHEAT ENGINE] " + c.getDisplayName() + " is using a cheat engine for the lamps!```");
			c.setTeleportToX(2086);
			c.setTeleportToY(4466);
			c.forceLogout();
			ConnectedFrom.addConnectedFrom(c, c.connectedFrom);
			Discord.writeCheatEngineMessage("```I Kicked " + c.getDisplayName() + "```");
			Discord.writeCheatEngineMessage("I Kicked " + c.getDisplayName() + "```");
			c.heightLevel = 0;
			c.getPA().closeAllWindows();
			return true;
		}

}