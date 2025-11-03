package io.zaryx.content.commands.all;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.zaryx.Server;
import io.zaryx.content.commands.Command;
import io.zaryx.content.dialogue.DialogueBuilder;
import io.zaryx.content.dialogue.DialogueOption;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.entity.player.Right;
import io.zaryx.sql.MainSql.StoreDonation;
import io.zaryx.sql.donation.query.ClaimDonationsQuery;
import io.zaryx.sql.donation.model.DonationItem;
import io.zaryx.sql.donation.model.DonationItemList;
import io.zaryx.sql.donation.query.GetDonationsQuery;
import io.zaryx.util.logging.player.DonatedLog;

/**
 * Changes the password of the player.
 *
 * @author Emiel
 *
 */
public class Claim extends Command {

	public static void claimDonations(Player player) {
		Server.getDatabaseManager().exec(Server.getConfiguration().getStoreDatabase(), (context, connection) -> {
			DonationItemList donationItemList = new GetDonationsQuery(player.getLoginName()).execute(context, connection);

			player.addQueuedAction(plr -> {
				List<DonationItem> claimed = new ArrayList<>();

				try {
					donationItemList.newDonations().forEach(item -> {
						if (giveDonationItem(player, item)) {
							claimed.add(item);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (!claimed.isEmpty()) {
						Server.getDatabaseManager().exec(Server.getConfiguration().getStoreDatabase(), new ClaimDonationsQuery(player, claimed));
					}
				}
			});

			return null;
		});
	}

	public static boolean giveDonationItem(Player plr, DonationItem item) {
		int itemId = item.getItemId();
		int itemQuantity = item.getItemAmount();
		if (plr.getItems().hasRoomInInventory(itemId, itemQuantity)) {
			plr.getItems().addItem(itemId, itemQuantity);
			Server.getLogging().write(new DonatedLog(plr, item));
			plr.getDonationRewards().increaseDonationAmount(item.getItemCost() * itemQuantity);
			plr.sendMessage("You've received x" + item.getItemAmount() + " " + item.getItemName());
			plr.start(new DialogueBuilder(plr).option("Would you like to announce your donation?",
					new DialogueOption("Yes, show my support!", p -> {
						PlayerHandler.message(Right.STAFF_MANAGER, "@blu@[" + p.getDisplayName() + "]@pur@ has just donated for " + itemQuantity + " " + item.getItemName() + "!");
					}), new DialogueOption("No thank you, keep me out the loop.", p -> p.getPA().closeAllWindows())));
			return true;
		} else {
			plr.sendMessage("Not enough room in inventory to claim " + item.getItemName() + ", make space and try again.");
			return false;
		}
	}

	@Override
	public void execute(Player c, String commandName, String input) {
		new java.lang.Thread() {
			public void run() {
				new Thread(new StoreDonation(c)).start();
			}
		}.start();
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Claim your donated item.");
	}
}
