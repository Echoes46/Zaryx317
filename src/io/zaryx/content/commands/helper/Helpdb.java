package io.zaryx.content.commands.helper;

import io.zaryx.content.commands.Command;
import io.zaryx.content.help.HelpDatabase;
import io.zaryx.model.entity.player.Player;

/**
 * Opens an interface containing all help tickets.
 * 
 * @author Emiel
 */
public class Helpdb extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		HelpDatabase.getDatabase().openDatabase(c);
	}
}
