package io.zaryx.content.commands.all;

import java.util.Optional;

import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.mode.Mode;
import io.zaryx.model.entity.player.mode.ModeType;

public class Leaveraids extends Commands {

	@Override
	public void execute(Player player, String commandName, String input) {
		if (player.getRaidsInstance() == null) {
			player.sendErrorMessage("You are not in a raid...");
			return;
		}
		player.setRaidsInstance(null);
		if (player.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || player.getMode().equals(Mode.forType(ModeType.GROUP_WILDYMAN))) {
			player.getPA().spellTeleport(3183, 3725, 0, true);

		} else {
			player.getPA().spellTeleport(1234, 3567, 0,true);
		}
	}
	@Override
	public Optional<String> getDescription() {
		return Optional.of("Forces you to leave raids.");
	}
}


