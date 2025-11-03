package io.zaryx.content.commands.owner;

import com.google.common.collect.Lists;
import io.zaryx.content.commands.Command;
import io.zaryx.content.skills.dungeoneering.DungMain;
import io.zaryx.model.entity.player.Player;

import java.util.Optional;

public class TestDung extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        // Create a new dungeoneering instance
        DungMain dungInstance = new DungMain();

        // Start the dungeoneering instance with the player
        dungInstance.startDung(Lists.newArrayList(player), false);

        player.sendMessage("You have been put into a dungeoneering instance for testing.");
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Puts the player into a dungeoneering instance for testing");
    }
}