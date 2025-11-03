package io.zaryx.content.commands.admin;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

/**
 * Transform a given player into an npc.
 * 
 * @author Emiel
 *
 */
public class Unpnpc extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {

		c.isNpc = false;
		c.setUpdateRequired(true);
		c.appearanceUpdateRequired = true;
	}
}
