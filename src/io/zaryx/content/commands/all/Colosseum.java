package io.zaryx.content.commands.all;

import io.zaryx.Server;
import io.zaryx.content.commands.Command;
import io.zaryx.content.dialogue.DialogueBuilder;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;

import java.util.Optional;

/**
 * Teleport the player to home.
 *
 * @author Emiel
 */
public class Colosseum extends Command {

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
		if (Boundary.isIn(c, Boundary.AFK_ZONE)) {
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


		if (c.CollTimer > 0) {
			c.moveTo(new Position(1824, 3105, 0));
		} else {
			c.start(new DialogueBuilder(c).npc(4249, "You don't have anytime left in the colesseum!"));
		}

	}
	@Override
	public Optional<String> getDescription() {
		return Optional.of("Teleports you to home area");
	}

}
