package io.zaryx.model.entity.player.packets.dialogueoptions;

import com.google.common.collect.Lists;
import io.zaryx.content.fireofexchange.FireOfExchange;
import io.zaryx.content.achievement_diary.impl.ArdougneDiaryEntry;
import io.zaryx.content.achievement_diary.impl.FaladorDiaryEntry;
import io.zaryx.content.achievement_diary.impl.VarrockDiaryEntry;
import io.zaryx.content.items.TomeOfFire;
import io.zaryx.content.skills.slayer.TaskExtender;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.GameItem;
import io.zaryx.model.items.bank.BankPin;
import io.zaryx.util.Misc;

/*
 * @author Matt
 * Three Option Dialogue actions
 */

public class ThreeOptions {

	/*
	 * Handles all first options on 'Three option' dialogues.
	 */
	public static void handleOption1(Player c) {
		switch (c.dialogueAction) {
		case 265:
			TomeOfFire.store(c);
            break;
		case 152:
			c.getDH().sendDialogues(153, 1603);
			break;
		case 1428:
			c.getPrestige().openPrestige();
			break;
		case 130135:
			c.currentExchangeItemAmount = c.getItems().getItemAmount(c.currentExchangeItem);
			FireOfExchange.exchangeItemForPoints(c);
			c.getPA().closeAllWindows();
			if (!c.getRecentlyDissolvedItems().isEmpty() && !c.getRecentlyDissolvedPrices().isEmpty()) {
				if (c.getRecentlyDissolvedItems().size() > 6 && c.getRecentlyDissolvedPrices().size() > 6) {
					c.getRecentlyDissolvedItems().remove(0);
					c.getRecentlyDissolvedPrices().remove(0);
				}
				for (int i = 0; i < c.getRecentlyDissolvedItems().size(); i++) {
					c.getPA().itemOnInterface(c.getRecentlyDissolvedItems().get(i), 1, 33422+i, 0);
					c.getPA().sendString(33428+i, String.valueOf(Misc.formatCoins(c.getRecentlyDissolvedPrices().get(i))));
				}
			}
			c.getPA().sendString(33415, String.valueOf(Misc.formatCoins(c.totalEarnedExchangePoints)));
			c.getItems().sendItemContainer(33403, Lists.newArrayList(new GameItem(23, 1)));
			c.getPA().sendInterfaceSet(33400, 33404);
			c.getItems().sendInventoryInterface(33405);
			c.getPA().sendFrame126("@gre@" + c.exchangePoints, 33410);
			c.getPA().sendFrame126("@red@0", 33409);
			break;
			case 33900:
				if (c.getSlayer().getTask().isPresent()) {
					c.getDH().sendStatement("Please finish your current task first.");
					return;
				}
				c.getSlayer().createNewTask(7663, false);
				c.lastTask = c.getSlayer().getTask().get().getPrimaryName();
				c.getDH().sendNpcChat("You have been assigned "+ c.getSlayer().getTaskAmount() + " " + c.getSlayer().getTask().get().getPrimaryName(), "in the wilderness.");
				c.nextChat = -1;
				break;
		case 809: // Withdraw
			// TODO: withdraw 10
			break;
		case 811: // Deposit
			// TODO: withdraw 10
			break;
		case 806:
			c.getDH().sendDialogues(811, 6773);
			break;

		case 71: // Jad, sell cape
			if (!c.getItems().playerHasItem(6570)) {
				c.sendMessage("You do not have a firecape.");
				return;
			}
			c.getItems().deleteItem(6570, 1);
			c.getItems().addItem(6529, 8_000);
			c.getPA().removeAllWindows();
			break;
			case 67677:// corp beast main entrance
				c.getPA().movePlayer(2974, c.getY(), 2);
				c.getPA().closeAllWindows();
				break;
			case 9800://attk
				c.getPA().startTeleport(2673, 3710, 0, "modern", false);
				c.getPA().closeAllWindows();
				break;
			case 9801://str
				c.getPA().startTeleport(2673, 3710, 0, "modern", false);
				c.getPA().closeAllWindows();
				break;
			case 9802://def
				c.getPA().startTeleport(2673, 3710, 0, "modern", false);
				c.getPA().closeAllWindows();
				break;
			case 9803://rng
				c.getPA().startTeleport(2673, 3710, 0, "modern", false);				c.getPA().closeAllWindows();
				break;
			case 9804://pray
				c.getPA().startTeleport(3087, 3515, 0, "modern", false);
				c.getPA().closeAllWindows();
				break;
			case 9805://mage
				c.getPA().startTeleport(2673, 3710, 0, "modern", false);
				c.getPA().closeAllWindows();
				break;
			case 9806://rc
				c.getPA().startTeleport(3093, 3517, 0, "modern", false);
				c.getPA().closeAllWindows();
				break;
			case 9807://agility
				c.getPA().startTeleport(3095, 3492, 0, "modern", false);
				c.getPA().closeAllWindows();
				break;
			case 9808://herb
				c.getPA().startTeleport(3111, 3516, 0, "modern", false);
				c.getPA().closeAllWindows();
				break;
			case 9809://thieving
				c.getPA().startTeleport(3093, 3461, 0, "modern", false);
				c.getPA().closeAllWindows();
				break;
			case 9810://craft
				c.getPA().startTeleport(3075, 3459, 0, "modern", false);
				c.getPA().closeAllWindows();
				break;
			case 9811://fletch
				c.getPA().startTeleport(3078, 3479, 0, "modern", false);
				c.getPA().closeAllWindows();
				break;
			case 9812://slayer
				c.getPA().startTeleport(3111, 3494, 0, "modern", false);
				c.getPA().closeAllWindows();
				break;
			case 9813://mining
				c.getPA().startTeleport(3094, 3478, 0, "modern", false);
				c.getPA().closeAllWindows();
				break;
			case 9814://smithing
				c.getPA().startTeleport(3092, 3467, 0, "modern", false);
				c.getPA().closeAllWindows();
				break;
			case 9815://fish
				c.getPA().startTeleport(3108, 3482, 0, "modern", false);
				c.getPA().closeAllWindows();
				break;
			case 9816://cook
				c.getPA().startTeleport(3075, 3459, 0, "modern", false);
				c.getPA().closeAllWindows();
				break;
			case 9817://firemaking
				c.getPA().startTeleport(3072, 3461, 0, "modern", false);
				c.getPA().closeAllWindows();
				break;
			case 9818://woodcut
				c.getPA().startTeleport(1589, 3497, 0, "modern", false);				c.getPA().closeAllWindows();
				break;
			case 9819://farming
				c.getPA().startTeleport(3053, 3303, 0, "modern", false);
				c.getPA().closeAllWindows();
				break;
			case 9820://hunter
				c.getPA().startTeleport(3563, 4005, 0, "modern", false);
				c.getPA().closeAllWindows();
				break;



		case 55:
			c.getCT().seas("TEN");
			c.dialogueAction = -1;
			c.getPA().removeAllWindows();
			break;
		case 56:
			c.getCT().swamp("TEN");
			c.dialogueAction = -1;
			c.getPA().removeAllWindows();
			break;
		}
		if (c.dialogueAction == 137) {
			c.getPA().c.itemAssistant.openUpBank();
			return;
		}
		if (c.dialogueAction == 126) {
			c.getPA().startTeleport(3039, 4835, 0, "modern", false);
			c.dialogueAction = -1;
			c.teleAction = -1;
			return;
		}
		switch (c.teleAction) {
		case 2:
			c.getPA().spellTeleport(1571, 3656, 0, false);
			break;
		}
		if (c.dialogueAction == 100) {
			c.getShops().openShop(80);
			return;
		}
		if (c.dialogueAction == 2245) {
			c.getPA().startTeleport(2110, 3915, 0, "modern", false);
			c.sendMessage("High Priest teleported you to @red@Lunar Island@bla@.");
			c.getPA().closeAllWindows();
		}
		if (c.dialogueAction == 508) {
			c.getDH().sendDialogues(1030, 925);
			return;
		}
		if (c.teleAction == 2) {
			// brim
			c.getPA().spellTeleport(1571, 3656, 0, false);
		}
		if (c.dialogueAction == 502) {
			c.getDH().sendDialogues(1030, 925);
			return;
		}
		if (c.dialogueAction == 251) {
			c.getPA().c.itemAssistant.openUpBank();
		}
		if (c.teleAction == 200) {
			c.getPA().spellTeleport(2662, 2652, 0, false);
		}
		if (c.doricOption) {
			c.getDH().sendDialogues(306, 284);
			c.doricOption = false;
		}
	}

	/*
	 * Handles all 2nd options on 'Three option' dialogues.
	 */
	public static void handleOption2(Player c) {

		switch (c.dialogueAction) {
		case 265:
			TomeOfFire.remove(c);
		    break;
		case 1428:
			c.getPrestige().openShop();
			break;
		case 33900:
			// TODO: Slayer task extension
			TaskExtender.Extend(c);
				break;
		case 809: // Withdraw
			// TODO: withdraw 100
			break;
		case 811: // Deposit
			// TODO: withdraw 100
			break;
		case 130135:
			c.currentExchangeItemAmount = 1;
			FireOfExchange.exchangeItemForPoints(c);
			c.getPA().closeAllWindows();
			if (!c.getRecentlyDissolvedItems().isEmpty() && !c.getRecentlyDissolvedPrices().isEmpty()) {
				if (c.getRecentlyDissolvedItems().size() > 6 && c.getRecentlyDissolvedPrices().size() > 6) {
					c.getRecentlyDissolvedItems().remove(0);
					c.getRecentlyDissolvedPrices().remove(0);
				}
				for (int i = 0; i < c.getRecentlyDissolvedItems().size(); i++) {
					c.getPA().itemOnInterface(c.getRecentlyDissolvedItems().get(i), 1, 33422+i, 0);
					c.getPA().sendString(33428+i, String.valueOf(Misc.formatCoins(c.getRecentlyDissolvedPrices().get(i))));
				}
			}
			c.getPA().sendString(33415, String.valueOf(Misc.formatCoins(c.totalEarnedExchangePoints)));
			c.getItems().sendItemContainer(33403, Lists.newArrayList(new GameItem(23, 1)));
			c.getPA().sendInterfaceSet(33400, 33404);
			c.getItems().sendInventoryInterface(33405);
			c.getPA().sendFrame126("@gre@" + c.exchangePoints, 33410);
			c.getPA().sendFrame126("@red@0", 33409);
			break;

			case 806:
			c.getDH().sendDialogues(809, 6773);
			break;
		case 71: // Jad, keep cape
			c.getPA().removeAllWindows();
			break;

			case 9800://attk
				c.getSI().attackComplex(1);
				c.getSI().selected = 0;
				break;
			case 9801://str
					c.getSI().strengthComplex(1);
					c.getSI().selected = 1;
				break;
			case 9802://def
				c.getSI().defenceComplex(1);
				c.getSI().selected = 2;
				break;
			case 9803://rng
				c.getSI().rangedComplex(1);
				c.getSI().selected = 3;
				break;
			case 9804://pray
				c.getSI().prayerComplex(1);
				c.getSI().selected = 4;
				break;
			case 9805://mage
				c.getSI().magicComplex(1);
				c.getSI().selected = 5;
				break;
			case 9806://rc
				c.getSI().runecraftingComplex(1);
				c.getSI().selected = 6;
				break;
			case 9807://agility
				c.getSI().agilityComplex(1);
				c.getSI().selected = 8;
				break;
			case 9808://herb
				c.getSI().herbloreComplex(1);
				c.getSI().selected = 9;
				break;
			case 9809://thieving
				c.getSI().thievingComplex(1);
				c.getSI().selected = 10;
				break;
			case 9810://craft
				c.getSI().craftingComplex(1);
				c.getSI().selected = 11;
				break;
			case 9811://fletch
				c.getSI().fletchingComplex(1);
				c.getSI().selected = 12;
				break;
			case 9812://slayer
				c.getSI().slayerComplex(1);
				c.getSI().selected = 13;
				break;
			case 9813://mining
				c.getSI().miningComplex(1);
				c.getSI().selected = 14;
				break;
			case 9814://smithing
				c.getSI().smithingComplex(1);
				c.getSI().selected = 15;
				break;
			case 9815://fish
				c.getSI().fishingComplex(1);
				c.getSI().selected = 16;
				break;
			case 9816://cook
				c.getSI().cookingComplex(1);
				c.getSI().selected = 17;
				break;
			case 9817://firemaking
				c.getSI().firemakingComplex(1);
				c.getSI().selected = 18;
				break;
			case 9818://woodcut
				c.getSI().woodcuttingComplex(1);
				c.getSI().selected = 19;
				break;
			case 9819://farming
				c.getSI().farmingComplex(1);
				c.getSI().selected = 20;
				break;
			case 9820://hunter
				c.getSI().hunterComplex(1);
				c.getSI().selected = 21;
				break;

		case 55:
			c.getCT().seas("HUNDRED");
			c.getPA().removeAllWindows();
			break;
		case 56:
			c.getCT().swamp("HUNDRED");
			c.getPA().removeAllWindows();
			break;
		}
		BankPin pin = c.getBankPin();
		if (c.dialogueAction == 137) {
			pin = c.getBankPin();
			if (!pin.getPin().isEmpty()) {
				c.sendMessage("You already have a bank pin.");
				c.getPA().removeAllWindows();
			} else {
				pin.open(1);
			}
			return;
		}
		if (c.dialogueAction == 126) {
			if (Boundary.isIn(c, Boundary.VARROCK_BOUNDARY)) {
				c.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.TELEPORT_ESSENCE_VAR);
			}
			if (Boundary.isIn(c, Boundary.ARDOUGNE_BOUNDARY)) {
				c.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.TELEPORT_ESSENCE_ARD);
			}
			if (Boundary.isIn(c, Boundary.FALADOR_BOUNDARY)) {
				c.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.TELEPORT_ESSENCE_FAL);
			}
			c.getPA().startTeleport(2929, 4813, 0, "modern", false);
			c.dialogueAction = -1;
			c.teleAction = -1;
			return;
		}
		switch (c.teleAction) {
		case 2:
			c.getPA().spellTeleport(1663, 3527, 0, false);
			c.teleAction = -1;
			break;
		}
		if (c.dialogueAction == 100) {
			c.getDH().sendDialogues(545, 315);
			return;
		}
		if (c.dialogueAction == 2245) {
			c.getPA().startTeleport(3230, 2915, 0, "modern", false);
			c.sendMessage("High Priest teleported you to @red@Desert Pyramid@bla@.");
			c.getPA().closeAllWindows();
		}
		if (c.dialogueAction == 508) {
			c.getDH().sendDialogues(1027, 925);
			return;
		}
		if (c.teleAction == 2) {
			// Tav
			c.getPA().spellTeleport(1663, 3527, 0, false);
		}
		if (c.dialogueAction == 502) {
			c.getDH().sendDialogues(1027, 925);
			return;
		}
		if (c.teleAction == 200) {
			c.getPA().spellTeleport(3365, 3266, 0, false);

		}
		if (c.doricOption) {
			c.getDH().sendDialogues(303, 284);
			c.doricOption = false;
		}
	}

	/*
	 * Handles all 3rd options on 'Three option' dialogues.
	 */
	public static void handleOption3(Player c) {
		switch (c.dialogueAction) {
		case 265:
			c.getPA().removeAllWindows();
			break;
		case 809: // Withdraw
			// TODO: withdraw all
			break;
		case 811: // Deposit
			// TODO: withdraw all
			break;
		case 33900:
			c.getPA().closeAllWindows();

			break;
		case 130135:
			c.currentExchangeItemAmount = 1;
			c.sendMessage("You decide to not destroy your item for points.");
			c.getPA().closeAllWindows();
			if (!c.getRecentlyDissolvedItems().isEmpty() && !c.getRecentlyDissolvedPrices().isEmpty()) {
				if (c.getRecentlyDissolvedItems().size() > 6 && c.getRecentlyDissolvedPrices().size() > 6) {
					c.getRecentlyDissolvedItems().remove(0);
					c.getRecentlyDissolvedPrices().remove(0);
				}
				for (int i = 0; i < c.getRecentlyDissolvedItems().size(); i++) {
					c.getPA().itemOnInterface(c.getRecentlyDissolvedItems().get(i), 1, 33422+i, 0);
					c.getPA().sendString(33428+i, String.valueOf(Misc.formatCoins(c.getRecentlyDissolvedPrices().get(i))));
				}
			}
			c.getPA().sendString(33415, String.valueOf(Misc.formatCoins(c.totalEarnedExchangePoints)));
			c.getItems().sendItemContainer(33403, Lists.newArrayList(new GameItem(23, 1)));
			c.getPA().sendInterfaceSet(33400, 33404);
			c.getItems().sendInventoryInterface(33405);
			c.getPA().sendFrame126("@gre@" + c.exchangePoints, 33410);
			c.getPA().sendFrame126("@red@0", 33409);
			break;
		case 806:
			c.getDH().sendDialogues(807, 6773);
			break;
		case 71: // Bargain cape
			c.getDH().sendDialogues(72, 2180);
			break;
		case 55:
			c.getCT().seas("THOUSAND");
			c.getPA().removeAllWindows();
			break;
		case 56:
			c.getCT().swamp("THOUSAND");
			c.getPA().removeAllWindows();
			break;
			case 9800://attk
				c.getPA().closeAllWindows();
				break;
			case 9801://str
				c.getPA().closeAllWindows();
				break;
			case 9802://def
				c.getPA().closeAllWindows();
				break;
			case 9803://rng
				c.getPA().closeAllWindows();
				break;
			case 9804://pray
				c.getPA().closeAllWindows();
				break;
			case 9805://mage
				c.getPA().closeAllWindows();
				break;
			case 9806://rc
				c.getPA().closeAllWindows();
				break;
			case 9807://agility
				c.getPA().closeAllWindows();
				break;
			case 9808://herb
				c.getPA().closeAllWindows();
				break;
			case 9809://thieving
				c.getPA().closeAllWindows();
				break;
			case 9810://craft
				c.getPA().closeAllWindows();
				break;
			case 9811://fletch
				c.getPA().closeAllWindows();
				break;
			case 9812://slayer
				c.getPA().closeAllWindows();
				break;
			case 9813://mining
				c.getPA().closeAllWindows();
				break;
			case 9814://smithing
				c.getPA().closeAllWindows();
				break;
			case 9815://fish
				c.getPA().closeAllWindows();
				break;
			case 9816://cook
				c.getPA().closeAllWindows();
				break;
			case 9817://firemaking
				c.getPA().closeAllWindows();
				break;
			case 9818://woodcut
				c.getPA().closeAllWindows();
				break;
			case 9819://farming
				c.getPA().closeAllWindows();
				break;
			case 9820://hunter
				c.getPA().closeAllWindows();
				break;
		case 126:
			if (c.dialogueAction == 126) {
				if (c.getItems().getItemCount(5509, true) == 1) {
					c.getDH().sendNpcChat("You already seem to have a pouch.");
				} else {
					c.getItems().addItem(5509, 1);
					c.getDH().sendItemStatement("The mage hands you a pouch", 5509);
					c.sendMessage("[Rc Pouch] Kill npcs with the pouch in inventory to upgrade it! 1\100 chance");
				}
			}
			break;
		}
		if (c.dialogueAction == 137) {
			c.getPA().removeAllWindows();
			return;
		}
		switch (c.teleAction) {
		case 2:
			c.getPA().spellTeleport(1262, 3501, 0, false);
			return;
		}
		if (c.dialogueAction == 14400 || c.dialogueAction == 100) {
			c.getPA().closeAllWindows();
		}
		if (c.dialogueAction == 2245) {
			c.getPA().closeAllWindows();
		}
		if (c.dialogueAction == 508) {
			c.nextChat = 0;
			c.getPA().closeAllWindows();
		}
		if (c.dialogueAction == 502 || c.dialogueAction == 1428) {
			c.nextChat = 0;
			c.getPA().closeAllWindows();
		}
		if (c.teleAction == 2) {
			c.getPA().spellTeleport(1262, 3501, 0, false);
		}
		if (c.dialogueAction == 251) {
			c.getDH().sendDialogues(1015, 394);
		}
		if (c.teleAction == 200) {
			c.getPA().spellTeleport(2439, 5169, 0, false);
			c.sendMessage("Use the cave entrance to start.");
		}
		if (c.doricOption) {
			c.getDH().sendDialogues(299, 284);
		}
	}

}
