package io.zaryx.content.items;

import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.ClientGameTimer;

import java.util.concurrent.TimeUnit;


public class CluescrollRateIncreaseScroll {

	private static final long TIME = TimeUnit.MINUTES.toMillis(30) / 600;

	public static void openScroll(Player player) {
		if (player.fasterCluesScroll) {
			player.sendMessage("You already have a bonus skill pet rate going.");
			return;
		}

		player.fasterCluesScroll = true;
		player.fasterCluesTicks = TIME;
		syncTimer(player);
	}

	public static void syncTimer(Player player) {
		if (player.fasterCluesTicks <= 0) {
			return;
		}
		player.getPA().sendGameTimer(ClientGameTimer.BONUS_CLUES, TimeUnit.SECONDS,
				remainingSeconds(player.fasterCluesTicks));
	}

	static int remainingSeconds(long ticks) {
		return (int) Math.min(Integer.MAX_VALUE, Math.max(0L, (ticks * 600L + 999L) / 1000L));
	}
	

}
	
