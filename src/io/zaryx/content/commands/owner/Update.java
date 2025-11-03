package io.zaryx.content.commands.owner;

import java.util.Objects;

import io.zaryx.Server;
import io.zaryx.content.commands.Command;
import io.zaryx.content.wogw.Wogw;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.zaryx.model.multiplayersession.MultiplayerSessionStage;
import io.zaryx.model.multiplayersession.MultiplayerSessionType;
import io.zaryx.model.multiplayersession.duel.DuelSession;

/**
 * Start the update timer and update the server.
 * 
 * @author Emiel
 *
 */
public class Update extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		int seconds = Integer.parseInt(input);
		if (seconds < 15) {
			c.sendMessage("The timer cannot be lower than 15 seconds so other operations can be sorted.");
			seconds = 15;
		}
		PlayerHandler.updateSeconds = seconds;
		PlayerHandler.updateAnnounced = false;
		PlayerHandler.updateRunning = true;
		PlayerHandler.updateStartTime = System.currentTimeMillis();
		Wogw.save();
		for (Player player : PlayerHandler.players) {
			if (player == null) {
				continue;
			}
			Player client = player;
			if (client.getPA().viewingOtherBank) {
				client.getPA().resetOtherBank();
				client.sendMessage("An update is now occuring, you cannot view banks.");
			}
			DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(client, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(duelSession)) {
				if (duelSession.getStage().getStage() == MultiplayerSessionStage.FURTHER_INTERATION) {
					if (!duelSession.getWinner().isPresent()) {
						duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
						duelSession.getPlayers().forEach(p -> {
							p.sendMessage("The duel has been cancelled by the server because of an update.");
							duelSession.moveAndClearAttributes(p);
						});
					}
				} else if (duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
					duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
					duelSession.getPlayers().forEach(p -> {
						p.sendMessage("The duel has been cancelled by the server because of an update.");
						duelSession.moveAndClearAttributes(p);
					});
				}
			}
			player.getPA().sendUpdateTimer();
		}
	}
}
