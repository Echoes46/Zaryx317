package io.zaryx.content.commands.all;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.npc.pets.PetHandler;
import io.zaryx.model.entity.player.Player;

import java.util.Optional;

/** Toggles the summoned companion model while keeping its active benefits. */
public class Hidepet extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        if (!player.hasFollower || player.petSummonId <= 0) {
            player.sendMessage("You do not have a companion summoned.");
            return;
        }

        if (player.petHidden) {
            if (PetHandler.showPet(player)) {
                player.sendMessage("Your companion is visible again.");
            } else {
                player.sendMessage("Your companion could not be shown. Please try again.");
            }
            return;
        }

        if (PetHandler.hidePet(player)) {
            player.sendMessage("Your companion is now hidden. Its active perks remain enabled.");
        } else {
            player.sendMessage("Your companion could not be hidden.");
        }
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Toggles your summoned companion's visibility");
    }
}
