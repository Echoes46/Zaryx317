package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

/**
 * Open a specific interface.
 * 
 * @author Emiel
 *
 */
public class Interface extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		try {
			int a = Integer.parseInt(input);
			c.getPA().showInterface(a);
		} catch (Exception e) {
			c.sendMessage("::interface ####");
		}
	}
}
