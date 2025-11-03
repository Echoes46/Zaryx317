package io.zaryx.content.commands.admin;

import io.zaryx.Configuration;
import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

/**
 * Toggles whether the Duel Arena is enabled or not.
 * 
 * @author Emiel
 */
public class Duelarena extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		Configuration.NEW_DUEL_ARENA_ACTIVE = !Configuration.NEW_DUEL_ARENA_ACTIVE;
		c.sendMessage("The duel arena is currently " + (Configuration.NEW_DUEL_ARENA_ACTIVE ? "Enabled" : "Disabled") + ".");
	}
}
