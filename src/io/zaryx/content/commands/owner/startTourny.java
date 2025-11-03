package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.content.tournaments.TourneyManager;
import io.zaryx.model.entity.player.Player;

public class startTourny extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		TourneyManager tourny = TourneyManager.getSingleton();
		tourny.init();
		tourny.openLobby();
	}

}
