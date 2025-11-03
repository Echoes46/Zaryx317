package io.zaryx.content.commands.owner;

import io.zaryx.Configuration;
import io.zaryx.Server;
import io.zaryx.content.combat.Hitmark;
import io.zaryx.content.commands.Command;
import io.zaryx.content.customs.CustomItemHandler;
import io.zaryx.content.dailyrewards.DailyRewardContainer;
import io.zaryx.content.donor.DonorVault;
import io.zaryx.content.fireofexchange.FireOfExchangeBurnPrice;
import io.zaryx.content.minigames.coinflip.CoinFlip;
import io.zaryx.content.minigames.coinflip.CoinFlipJson;
import io.zaryx.content.vote_panel.VotePanelManager;
import io.zaryx.content.wogw.Wogw;
import io.zaryx.model.collisionmap.doors.DoorDefinition;
import io.zaryx.model.definitions.ItemDef;
import io.zaryx.model.definitions.ItemStats;
import io.zaryx.model.definitions.ShopDef;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.npc.NPCHandler;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.world.ShopHandler;
import io.zaryx.sql.refsystem.RefManager;

import java.io.IOException;

/**
 * Reloading certain objects by {String input}
 * 
 * @author Matt
 */

public class Reload extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		switch (input) {

			case "scan":
				for (NPC npc : NPCHandler.npcs) {
					if (npc == null) {
						continue;
					}
					if (npc.getPosition().withinDistance(player.getPosition(), 15)) {
						player.sendErrorMessage("Null npc ? " + npc.getNpcId());
						npc.appendDamage(npc.getHealth().getMaximumHealth(), Hitmark.HIT);
						npc.unregisterInstant();
					}
				}
				break;

			case "":
				player.sendMessage("@red@Usage: ::reload doors, drops, items, objects, shops or npcs");
				break;

			case "coinflip":
				try {
					CoinFlipJson.loadJson();
				} catch (IOException e) {
					player.sendMessage("Error loading coinflip data, check the server output!");
					e.printStackTrace();
				}
				break;

			case "dailyrewards":
				try {
					DailyRewardContainer.load();
					player.sendMessage("Loaded daily rewards.");
				} catch (Exception e) {
					player.sendMessage("Error loading daily rewards, check the server output!");
					e.printStackTrace();
				}
				break;

			case "referralcodes":
				try {
//					ReferralCode.load();
					RefManager.loadReferralRewards();
					player.sendMessage("Loaded referral codes.");
				} catch (Exception e) {
					player.sendMessage("Error loading referrals, check the server output!");
					e.printStackTrace();
				}
				break;

			case "doors":
				try {
					DoorDefinition.load();
					player.sendMessage("@blu@Reloaded Doors.");
				} catch (IOException e) {
					e.printStackTrace();
					player.sendMessage("@blu@Unable to reload doors, check console.");
				}
				break;

			case "drops":
				try {
					Server.getDropManager().read();
					player.sendMessage("@blu@Reloaded Drops.");
				} catch (Exception e) {
					player.sendMessage("@red@Error reloading drops!");
					e.printStackTrace();
				}

				break;

			case "items":
				try {
					ItemDef.load();
					ItemStats.load();
					CustomItemHandler.handleCustomItem();
					DonorVault.handleStatics();
					CoinFlip.itemActionHandler();
					player.sendMessage("@blu@Reloaded Items.");
				} catch (Exception e) {
					player.sendMessage("@blu@Unable to reload items, check console.");
					e.printStackTrace();
				}
				break;

			case "wogw":
				Wogw.init();
				break;
			case "objects":
				try {
					Server.getGlobalObjects().reloadObjectFile(player);
					player.sendMessage("@blu@Reloaded Objects.");
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;

			case "shops":
				try {
					FireOfExchangeBurnPrice.createBurnPriceShop();
					Server.shopHandler = new ShopHandler();
					ShopDef.load();
					ShopHandler.load();
					player.sendMessage("@blu@Reloaded Shops");
				} catch (Exception e) {
					player.sendMessage("Error occurred, check console.");
					e.printStackTrace();
				}
				break;

			case "npcs":
				Server.npcHandler = null;
				Server.npcHandler = new NPCHandler();
				player.sendMessage("@blu@Reloaded NPCs");
				break;

			case "votes" :
				VotePanelManager.init();
				player.sendMessage("@blu@Reloaded Votes");
				break;

			case "punishments":
				try {
					Server.getPunishments().initialize();
					player.sendMessage("@blu@Reloaded Punishments.");
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;

			case "looting":
				Configuration.BAG_AND_POUCH_PERMITTED = !Configuration.BAG_AND_POUCH_PERMITTED;
				player.sendMessage(""+(Configuration.BAG_AND_POUCH_PERMITTED ? "Enabled" : "Disabled" +"") + " bag and pouch.");
				break;

			case "tp":

				break;

		}
	}

}
