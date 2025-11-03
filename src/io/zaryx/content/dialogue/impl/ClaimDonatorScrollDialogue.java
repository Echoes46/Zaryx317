package io.zaryx.content.dialogue.impl;

import io.zaryx.content.dialogue.DialogueBuilder;
import io.zaryx.content.dialogue.DialogueOption;
import io.zaryx.model.Npcs;
import io.zaryx.model.entity.player.Player;

import java.util.Arrays;
import java.util.Optional;

public class ClaimDonatorScrollDialogue extends DialogueBuilder {

    public static boolean clickScroll(Player player, int itemId) {
        Optional<DonationScroll> scrollOptional = Arrays.stream(DonationScroll.values()).filter(scroll -> scroll.itemId == itemId).findFirst();
        if (scrollOptional.isPresent()) {
            player.start(new ClaimDonatorScrollDialogue(player, scrollOptional.get()));
            return true;
        } else {
            return false;
        }
    }

    private static final int NPC_ID = Npcs.ELDERLY_ELF;
    private final DonationScroll scroll;

    public ClaimDonatorScrollDialogue(Player player, DonationScroll scroll) {
        super(player);
        this.scroll = scroll;
        setNpcId(NPC_ID);
        npc("Are you sure you want to claim this scroll?", "You will claim $" + scroll.donationAmount + ".");
        option(new DialogueOption("Yes, claim $" + scroll.donationAmount + " scroll.", p -> claim()),
                new DialogueOption("Nevermind", p -> p.getPA().closeAllWindows()));
    }

    private void claim() {
        if (!getPlayer().getItems().playerHasItem(scroll.itemId))
            return;
        getPlayer().getItems().deleteItem(scroll.itemId, 1);
        getPlayer().gfx100(2259);
        getPlayer().donatorPoints += scroll.credits;
        getPlayer().amDonated += scroll.donationAmount;
        getPlayer().getPA().closeAllWindows();
        getPlayer().start(new DialogueBuilder(getPlayer()).setNpcId(NPC_ID).npc("Thank you for donating!",
                scroll.donationAmount + "$ has been added to your total donated",scroll.credits+
                        " credits have been added to your account."));
        getPlayer().updateRank();
    }

    public enum DonationScroll {
        ONE(956, 1, 1),
        FIVE(6769, 5, 5),
        TEN(2403, 10, 10),
        TWENTY_FIVE(2396, 25, 25),
        FIFTY(786, 50, 50),
        ONE_HUNDRED(761, 100, 100),
        TWO_FIFTY(607, 250, 250),
        FIVE_HUNDRED(608, 500, 500),

        CREDIT10(608, 10, -1),
        CREDIT50(608, 50, -1),
        CREDIT100(608, 100, -1)


        ;

        private final int itemId;
        private final int donationAmount;
        private final int credits;
        DonationScroll(int itemId, int donationAmount, int credits) {
            this.itemId = itemId;
            this.donationAmount = donationAmount;
            this.credits = credits;
        }

        public int getItemId() {
            return itemId;
        }

        public int getDonationAmount() {
            return donationAmount;
        }
    }
}
