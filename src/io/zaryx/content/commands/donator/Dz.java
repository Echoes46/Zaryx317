package io.zaryx.content.commands.donator;

import java.util.Optional;

import io.zaryx.content.commands.Command;
import io.zaryx.content.dialogue.DialogueBuilder;
import io.zaryx.content.dialogue.DialogueOption;
import io.zaryx.model.entity.player.Player;

/**
 * Teleports the player to the donator zone.
 *
 * @author Emiel
 */
public class Dz extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (c.inTrade || c.inDuel || c.getPosition().inWild()) {
			return;
		}
		if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
			c.sendMessage("@cr10@This player is currently at the pk district.");
			return;
		}

		c.start(new DialogueBuilder(c).option("Select the donor zone you wish to enter!",
				new DialogueOption("@bla@ Donator Zone (@gre@$25@bla@)",
						p -> teleportToZone(p, 25, 1759, 5469)),
				new DialogueOption("@bla@ Donator Zone (@blu@$250@bla@)",
						p -> teleportToZone(p, 250, 2604, 3874)),
				new DialogueOption("@bla@ Donator Zone (@yel@$1000@bla@)",
						p -> teleportToZone(p, 1000, 2604, 3874)),
				new DialogueOption("@bla@ Donator Zone (@whi@$2000@bla@)",
						p -> teleportToZone(p, 2000, 2406, 3803))));
	}

	private static void teleportToZone(Player player, int requiredDonation, int x, int y) {
		if (!player.getRights().hasStaffPosition() && player.amDonated < requiredDonation) {
			player.sendMessage("You need to have donated at least $" + requiredDonation + " to enter this zone.");
			return;
		}

		player.getPA().startTeleport(x, y, 0, "modern", false);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Teleports you to donator zone.");
	}

}
