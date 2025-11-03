package io.zaryx.content.commands.all;

import io.zaryx.content.commands.Command;
import io.zaryx.content.wildwarning.WildWarning;
import io.zaryx.model.entity.player.Player;

import java.util.Optional;

/**
 * Show the current position.
 * 
 * @author Noah
 *
 */
public class Wild extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		WildWarning.sendWildWarning(player, null);
	}
	@Override
	public Optional<String> getDescription() {
		return Optional.of("Open the wild warning interface.");
	}
}
