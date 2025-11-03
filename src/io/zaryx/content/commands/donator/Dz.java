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
				new DialogueOption("@bla@ Donator Zone (@gre@$25@bla@)", p -> {
					if (c.amDonated >= 24) {
						c.getPA().startTeleport(1759, 5469, 0, "modern", false);
					}
				}),
				new DialogueOption("@bla@ Donator Zone (@blu@$250@bla@)", p -> {
					if (c.amDonated >= 249) {
						c.getPA().startTeleport(2604, 3874, 0, "modern", false);
					}
				}),
					new DialogueOption("@bla@ Donator Zone (@yel@$1000@bla@)", p -> {
						if (c.amDonated >= 999) {
							c.getPA().startTeleport(2604, 3874, 0, "modern", false);
						}
					}),
						new DialogueOption("@bla@ Donator Zone (@whi@$2500@bla@)", p -> {
							if (c.amDonated >= 2499) {
								c.getPA().startTeleport(2406, 3803, 0, "modern", false);
							}
				})));
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Teleports you to donator zone.");
	}

}
