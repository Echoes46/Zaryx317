package io.zaryx.content.commands.all;

import java.util.Optional;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

/**
 * Changes the password of the player.
 * 
 * @author Emiel
 *
 */
public class Streak extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		c.sendMessage("@blu@ You have @red@"+c.getSlayer().getConsecutiveTasks()+" @blu@consecutive tasks.");
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Tells you your slayer consecutive tasks.");
	}
}
