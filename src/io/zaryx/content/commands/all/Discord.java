package io.zaryx.content.commands.all;

import java.util.Optional;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

/**
 * Opens the vote page in the default web browser.
 * 
 * @author Emiel
 */
public class Discord extends Command {

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Invites you to our Discord server");
	}

	@Override
	public void execute(Player player, String commandName, String input) {
		player.getPA().sendFrame126("https://discord.gg/Zaryx", 12000);
		
	}

}
