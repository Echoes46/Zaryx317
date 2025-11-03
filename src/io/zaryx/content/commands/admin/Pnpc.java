package io.zaryx.content.commands.admin;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

/**
 * Transform a given player into an npc.
 * 
 * @author Emiel
 *
 */
public class Pnpc extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		int npc = Integer.parseInt(input);
		
/*		if (npc > 9473) {
			c.sendMessage("Max npc id is: 9473");
			return;
		}*/
		
		c.npcId2 = npc;
		c.isNpc = true;
		c.setUpdateRequired(true);
		c.appearanceUpdateRequired = true;
	}
}
