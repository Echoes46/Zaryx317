package io.zaryx.content.commands.all;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.mode.Mode;
import io.zaryx.model.entity.player.mode.ModeType;

import java.util.Optional;

/**
 * Changes the password of the player.
 *
 * @author Emiel
 *
 */
public class Slayer extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (c.getPosition().inWild()) {
			c.sendMessage("You can only use this command outside the wilderness.");
			return;
		}
		if (c.getMode().equals(Mode.forType(ModeType.WILDYMAN))) {
			c.getPA().spellTeleport(3442, 3835, 0, true);
			return;
		} if (c.getMode().equals(Mode.forType(ModeType.GROUP_WILDYMAN))) {
			c.getPA().spellTeleport(3442, 3835, 0, true);
			return;
		}
		c.getPA().startTeleport(3111, 3493, 0, "modern", false);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Teleports you to slayer area.");
	}
}
