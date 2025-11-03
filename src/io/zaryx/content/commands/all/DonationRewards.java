package io.zaryx.content.commands.all;

import java.util.Optional;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

public class DonationRewards extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        player.getDonationRewards().openInterface();
    }

    public Optional<String> getDescription() {
        return Optional.of("Opens the Donation rewards interface.");
    }
}
