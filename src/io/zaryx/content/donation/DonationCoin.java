package io.zaryx.content.donation;

import io.zaryx.content.dialogue.DialogueBuilder;
import io.zaryx.content.dialogue.DialogueOption;
import io.zaryx.model.entity.player.Player;

/** Converts the physical donation currency into the points used by donor shops. */
public final class DonationCoin {
    public static final int ITEM_ID = 33251;

    private DonationCoin() { }

    public static void openConversion(Player player) {
        int available = player.getItems().getInventoryCount(ITEM_ID);
        if (available <= 0) {
            player.sendErrorMessage("You don't have any Zaryx Donation Coins.");
            return;
        }
        player.start(new DialogueBuilder(player).option(
                "Convert Zaryx Donation Coins into Donator Points?",
                new DialogueOption("Yes - one coin gives one point", p -> {
                    p.getPA().closeAllWindows();
                    p.getPA().sendEnterAmount("How many coins would you like to convert?", DonationCoin::convert);
                }),
                new DialogueOption("No thank you.", p -> p.getPA().closeAllWindows())));
    }

    public static int convert(Player player, int requestedAmount) {
        int available = player.getItems().getInventoryCount(ITEM_ID);
        int pointCapacity = Integer.MAX_VALUE - player.donatorPoints;
        int amount = Math.min(requestedAmount, Math.min(available, pointCapacity));
        if (amount <= 0) {
            player.sendErrorMessage(pointCapacity <= 0
                    ? "You cannot hold any more Donator Points."
                    : "Enter an amount greater than zero.");
            return 0;
        }

        player.getItems().deleteItem2(ITEM_ID, amount);
        player.donatorPoints += amount;
        if (player.getOutStream() != null) {
            player.getQuestTab().updateInformationTab();
        }
        player.sendMessage("@gre@Converted " + amount + " Zaryx Donation Coin"
                + (amount == 1 ? "" : "s") + " into " + amount + " Donator Point"
                + (amount == 1 ? "." : "s."));
        return amount;
    }
}
