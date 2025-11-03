package io.zaryx.content.commands.all;

import java.util.Optional;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.mode.Mode;
import io.zaryx.model.entity.player.mode.ModeType;

/**
 * Changes the password of the player.
 * 
 * @author Emiel
 *
 */
public class Train extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (c.getPosition().inWild()) {
			c.sendMessage("You can only use this command outside the wilderness.");
			return;
		}
		if (c.getMode().equals(Mode.forType(ModeType.GROUP_WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.WILDYMAN))) {
			c.sendMessage("You cannot access this area.");
			return;
		}
		c.getPA().startTeleport(2673, 3710, 0, "modern", false);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Takes you to rock crabs.");
	}
}
