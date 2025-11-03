package io.zaryx.content.commands.all;

import java.util.Optional;

import io.zaryx.content.bosses.hespori.HesporiSpawner;
import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

public class Worldevent extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		/*if (player.getMode().equals(Mode.forType(ModeType.GROUP_WILDYMAN)) || player.getMode().equals(Mode.forType(ModeType.WILDYMAN))) {
			player.sendMessage("You cannot access this area.");
			return;
		}*/
		if (HesporiSpawner.isSpawned()) {
			player.getPA().spellTeleport(3745, 4361, 0, false);
			player.setHesporiDamageCounter(0);
		} else {
			player.sendMessage("@red@[World Event] @bla@There is currently no world event going on.");
		}
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Teles you to world event.");
	}
}
