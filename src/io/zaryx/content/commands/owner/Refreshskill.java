package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

public class Refreshskill extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		c.getPA().refreshSkill(Integer.parseInt(input));
	}

}
