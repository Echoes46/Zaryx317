package io.zaryx.content.commands.all;

import io.zaryx.Configuration;
import io.zaryx.content.achievement.AchievementType;
import io.zaryx.content.achievement.Achievements;
import io.zaryx.content.battlepass.Pass;
import io.zaryx.content.bonus.DoubleExperience;
import io.zaryx.content.commands.Command;
import io.zaryx.content.commands.helper.vboss;
import io.zaryx.content.vote_panel.VotePanelManager;
import io.zaryx.content.vote_panel.VoteUser;
import io.zaryx.content.wogw.Wogw;
import io.zaryx.model.Items;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.ClientGameTimer;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.sql.MainSql.vote;
import io.zaryx.util.DateUtils;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Changes the password of the player.
 *
 * @author Emiel
 *
 */
public class Voted extends Command {

	public static final long XP_SCROLL_TICKS = TimeUnit.MINUTES.toMillis(30) / 600;
	private static final int GP_REWARD = 1_000_000;
	public static int globalVotes = 10;
	public static int totalVotes = 0;
	public static int durialVotes = 0;


	public static void claimVotes(Player player) {
		if (Configuration.VOTE_PANEL_ACTIVE) {
			votePanel(player);
		}
		int amt = 1;
		if (player.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33115)) {
			amt = 2;
		}

		player.bonusDmgTicks += (TimeUnit.MINUTES.toMillis(10) / 600);
		player.bonusDmg = true;
		player.getPA().sendGameTimer(ClientGameTimer.BONUS_DAMAGE, TimeUnit.MINUTES, (int) ((player.bonusDmgTicks / 100)));
		player.sendMessage("@gre@You've earned your self 10minutes of bonus damage!");
		rewards(player, amt);
		incrementGlobalVote(amt);
		incrementDurialCountdown(amt);
		Pass.addExperience(player, 5);

//		if (!AfkBoss.IPAddress.contains(player.getIpAddress())) {
//			AfkBoss.IPAddress.add(player.getIpAddress());
//		}
//		if (!AfkBoss.MACAddress.contains(player.getMacAddress())) {
//			AfkBoss.MACAddress.add(player.getMacAddress());
//		}
	}

	private static void votePanel(Player player) {
		VotePanelManager.addVote(player.getLoginName());
		VoteUser user = VotePanelManager.getUser(player);
		if (user != null) {
			if (player.getLastVotePanelPoint().isBefore(LocalDate.now())) { // Gain one point per day
				player.setLastVotePanelPoint(LocalDate.now());
				boolean oldStreakOverflow = user.getDayStreak() >= VoteUser.MAX_DAY_STREAK;
				user.incrementDayStreak();
				if (user.getDayStreak() > VoteUser.MAX_DAY_STREAK && !oldStreakOverflow) {
					user.resetDayStreak();
					user.incrementDayStreak();
				}

				if (user.getDayStreak() == VoteUser.MAX_DAY_STREAK || oldStreakOverflow) { //They just hit a 5 day streak (after incrementing) so reward them!
					player.getItems().addItemToBankOrDrop(22093, 1);
					player.getItems().addItemToBankOrDrop(6199, 1);
					player.sendMessage("@pur@One @gre@vote key @pur@has been added to your bank for a 5 vote streak!");
					player.sendMessage("@red@You just completed a 5 day voting streak!");
					user.resetDayStreak();
					if (oldStreakOverflow) {
						user.incrementDayStreak();
					}
				}

				player.debug("Gained one ::vpanel point, streak: {}.", "" + user.getDayStreak());
				VotePanelManager.saveToJSON();
			}
		}
	}

	private static void rewards(Player player, final int voteCount) {
		Achievements.increase(player, AchievementType.VOTER, voteCount);
		player.votePoints += voteCount;
		player.voteKeyPoints += voteCount;
		player.xpScroll = true;
		player.xpScrollTicks += XP_SCROLL_TICKS * voteCount;

		// Send bonus experience timer to player only if double exp isn't already activated (since they're non stackable).
		if (!DoubleExperience.isDoubleExperience()) {
			player.getPA().sendGameTimer(ClientGameTimer.BONUS_XP, TimeUnit.MINUTES, (int) ((player.xpScrollTicks / 100)));
		}

		// Coins
		boolean firstWeekOfMonth = DateUtils.isFirstWeekOfMonth();
		int amount = GP_REWARD * voteCount;
		if (firstWeekOfMonth) {
			amount *= 2;
			player.sendMessage("@red@You have gained " + voteCount + " voting point and extra gp for the first week of month!");
		} else {
			player.sendMessage("You have gained " + voteCount + " voting point and gp!");
		}
		player.getItems().addItemUnderAnyCircumstance(Items.COINS, amount);
	}

	private static void incrementDurialCountdown(final int voteCount) {
		final boolean firstWeekOfMonth = DateUtils.isFirstWeekOfMonth();
		final var header = firstWeekOfMonth ? "Double Vote Week" : "Vote";
		durialVotes += voteCount;

		if (durialVotes >= 50) {
			vboss.spawnBoss();
			durialVotes = 0;
		}
	}

	private static void incrementGlobalVote(final int voteCount) {
		final boolean firstWeekOfMonth = DateUtils.isFirstWeekOfMonth();
		final var header = firstWeekOfMonth ? "Double Vote Week" : "Vote";
		globalVotes += voteCount;
		totalVotes += voteCount;

		if (totalVotes >= 20) {
			vboss.spawnBoss();
			totalVotes = 0;
		}

		// Dividing by two because it counts both votes, don't have a better way atm
		if (globalVotes / 2 >= 50) {
			Wogw.votingBonus();
			globalVotes = 0;
			totalVotes = 0;
		} else if (globalVotes / 2 == 40) {
			PlayerHandler.executeGlobalMessage("@cr10@[@pur@" + header + "@bla@] Global votes are at 40, reach 50 for a server boost!");
		} else if (globalVotes / 2 == 20) {
			PlayerHandler.executeGlobalMessage("@cr10@[@pur@" + header + "@bla@] Global votes are at 20, reach 50 for a server boost!");
		} else if (globalVotes / 2 == 10) {
			PlayerHandler.executeGlobalMessage("@cr10@[@pur@" + header + "@bla@] Global votes are at 10, reach 50 for a server boost!");
		}
	}

	@Override
	public void execute(Player c, String commandName, String input) {
		if (c.getItems().freeSlots() < 1) {
			c.sendMessage("You need at least one free slots to use this command.");
			return;
		}

		if (c.hitDatabaseRateLimit(true))
			return;

		if (Boundary.isIn(c, Boundary.OUTLAST) || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_AREA) || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_LOBBY)

				|| Boundary.isIn(c, Boundary.FOREST_OUTLAST)
				|| Boundary.isIn(c, Boundary.SNOW_OUTLAST)
				|| Boundary.isIn(c, Boundary.ROCK_OUTLAST)
				|| Boundary.isIn(c, Boundary.BOUNTY_HUNTER_OUTLAST)
				|| Boundary.isIn(c, Boundary.FALLY_OUTLAST)
				|| Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST)
				|| Boundary.isIn(c, Boundary.SWAMP_OUTLAST)) {
			c.sendMessage("You cannot do this right now.");
			return;
		}
		new Thread(new vote(c)).start();
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Claim your voted reward.");
	}

	private static void sendNextVoteTime(Player player, String name, Timestamp timestamp) {
		if (timestamp == null) return;
		var lastVoteInstant = timestamp.toInstant();
		var nextVoteInstant = lastVoteInstant.plus(12, ChronoUnit.HOURS);
		var waitDuration = Duration.between(Instant.now(), nextVoteInstant);
		if (waitDuration.isNegative()) {
			player.sendMessage("You can vote at " + name + ".");
			return;
		}
		var formatted = format(waitDuration);
		player.sendMessage("You can vote at " + name + " in " + formatted + ".");
	}

	private static String format(final Duration duration) {
		var hours = duration.toHours();
		var minutes = duration.toMinutes();
		var seconds = duration.toSeconds();
		// Round everything up by 1.
		if (hours > 0) {
			var adjustedHours = hours + 1;
			return adjustedHours + " hours";
		} else if (minutes > 0) {
			var adjustedMinutes = minutes + 1;
			return adjustedMinutes + " minutes";
		} else {
			var adjustedSeconds = seconds + 1;
			return adjustedSeconds + " seconds";
		}
	}
}
