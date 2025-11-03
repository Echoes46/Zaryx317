package io.zaryx.content.commands.owner;

import io.zaryx.content.combat.Hitmark;
import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;

/**
 * Kill a player.
 *
 * @author Emiel
 */
public class Kill extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		Player player = PlayerHandler.getPlayerByDisplayName(input);
		if (player == null) {
			c.sendMessage("Player is null.");
			return;
		}
		if (player.invincible || player.inGodmode()) {
			player.invincible = false;
			player.setGodmode(false);
		}
		player.appendDamage(player.getHealth().getMaximumHealth(), Hitmark.HIT);
		player.sendMessage("You have been merked");
	}
}
