package io.zaryx.content.commands.admin;

import io.zaryx.Server;
import io.zaryx.content.commands.Command;
import io.zaryx.content.dialogue.impl.ClaimDonatorScrollDialogue;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;

import java.util.Arrays;
import java.util.Optional;

public class Givedonation extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        try {
            String[] data = input.split("-");
            Player recipient = PlayerHandler.getPlayerByDisplayName(data[0]);
            int scroll = Integer.parseInt(data[1]);
            Optional<ClaimDonatorScrollDialogue.DonationScroll> donationScroll = Arrays.stream(ClaimDonatorScrollDialogue.DonationScroll.values()).filter(it -> it.getDonationAmount() == scroll).findFirst();
            int amount = data.length == 3 ? Integer.parseInt(data[2]) : 1;

            if (recipient == null) {
                player.sendMessage("No player online with name: " + data[0]);
                return;
            }

            if (!donationScroll.isPresent()) {
                player.sendMessage("No donation scroll with price: $" + scroll);
                return;
            }

            ClaimDonatorScrollDialogue.DonationScroll donationScroll1 = donationScroll.get();

            player.sendMessage("Successfully gave " + donationScroll1.name().toLowerCase() + " x " + amount + " to '" + data[0] + "'.");

        } catch (Exception e) {
            if (Server.isDebug()) {
                e.printStackTrace();
            }
            player.sendMessage("Error occurred, usage: ::givedonation-name-10/25/50/100/250/500-amount");
        }
    }

    public Optional<String> getDescription() {
        return Optional.of("Give a donation to a player, functionally the same as a manual ::claim.");
    }

}
