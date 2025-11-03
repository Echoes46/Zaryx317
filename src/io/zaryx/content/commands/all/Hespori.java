package io.zaryx.content.commands.all;

import java.util.Optional;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

/**
 * Teleport the player to the mage bank.
 * 
 * @author Emiel
 */
public class Hespori extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
//    	c.getPA().sendFrame126("https://runecrest.com", 12000);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Opens up the Hespori boss guide.");
	}

}
