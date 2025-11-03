package io.zaryx.content.commands.admin;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

/**
 * Teleport the player to the given coordinates.
 * 
 * @author Emiel
 *
 */
public class Tele extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		String[] args = input.split(" ");
		if (args.length == 3) {
			c.getPA().movePlayer(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		} else if (args.length == 2) {
			c.getPA().movePlayer(Integer.parseInt(args[0]), Integer.parseInt(args[1]), c.heightLevel);
		}
	}
}
