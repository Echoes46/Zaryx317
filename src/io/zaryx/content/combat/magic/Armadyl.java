package io.zaryx.content.combat.magic;

import io.zaryx.model.Items;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.ImmutableItem;
import io.zaryx.util.Misc;

public class Armadyl {

    private static final int MAX_CHARGES = 12000;
    public static final int COMBAT_SPELL_INDEX = 103;

    public static boolean useItem(Player player, int item1, int item2) {
        if (item1 == Items.NATURE_RUNE && item2 == 84 || item2 == Items.NATURE_RUNE && item1 == 84) {
            charge(player);
            return true;
        }

        return false;
    }

    public static void checkChargesRemaining(Player player) {
        player.sendMessage("Your staff has " + Misc.insertCommas(player.getArmadylStaffCharge()) + " charges remaining.");
    }

    private static void charge(Player player) {
        if (player.getItems().playerHasItem(84)) {
            int ether = player.getItems().getItemAmount(Items.NATURE_RUNE);
            int currentCharges = player.getArmadylStaffCharge();

            if (ether == 0) {
                player.sendMessage("You don't have any Nature Runes!");
                return;
            }

            if (currentCharges >= MAX_CHARGES) {
                player.sendMessage("You have already stored 12,000 charges, you can't store any more!");
                return;
            }

            int chargesToAdd = ether;
            if (currentCharges + ether > MAX_CHARGES) {
                chargesToAdd = MAX_CHARGES - currentCharges;
            }

            if (player.getItems().playerHasItem(Items.NATURE_RUNE, chargesToAdd)) {
                player.getItems().deleteItem(Items.NATURE_RUNE, chargesToAdd);
                player.setArmadylStaffCharge(player.getArmadylStaffCharge() + chargesToAdd);
                player.sendMessage("You've added " + Misc.insertCommas(chargesToAdd) + " Nature Runes to your staff, you now have " + Misc.insertCommas(player.getArmadylStaffCharge()) + " charges.");
            }
        }
    }

    private static void uncharge(Player player) {
        if (player.getItems().playerHasItem(Items.THAMMARONS_SCEPTRE) || player.getItems().playerHasItem(27679)) {
            if (player.getArmadylStaffCharge() <= 2) {
                player.sendMessage("Your staff doesn't have any charges!");
                return;
            }

            if (player.getInventory().hasRoomInInventory(new ImmutableItem(Items.NATURE_RUNE, player.getArmadylStaffCharge()))) {
                player.getItems().addItem(Items.NATURE_RUNE, player.getArmadylStaffCharge());
                player.setArmadylStaffCharge(0);
                player.sendMessage("You uncharge your staff.");
            } else {
                player.sendMessage("You don't have enough space in your inventory to uncharge your staff.");
            }
        }
    }

}
