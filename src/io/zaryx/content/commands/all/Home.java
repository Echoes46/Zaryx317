package io.zaryx.content.commands.all;

import io.zaryx.Configuration;
import io.zaryx.Server;
import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.mode.Mode;
import io.zaryx.model.entity.player.mode.ModeType;

import java.util.Optional;

/**
 * Teleport the player to home.
 *
 * @author Emiel
 */
public class Home extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
			c.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
			return;
		}

		if (c.jailEnd > 1) {
			c.forcedChat("I'm trying to teleport away!");
			c.sendMessage("You are still jailed!");
			return;
		}
		if	(Boundary.isIn(c, Boundary.AFK_ZONE)) {
			c.teleporting = false;
			c.sendMessage("You must use the gate to exit the afk zone.");
			return;
		}
		if (c.getPosition().inWild() && c.wildLevel > 20) {
			if (c.petSummonId != 10533) {
				c.sendMessage("You can't use this command in the wilderness.");
				return;
			}
		}



		if (c.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.GROUP_WILDYMAN))) {
			c.getPA().spellTeleport(3466, 3837, 0, true);
		} else {
			c.getPA().spellTeleport(Configuration.START_LOCATION_X, Configuration.START_LOCATION_Y, 0, true);
		}
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Teleports you to home area");
	}

}
