package io.zaryx.content.commands.all;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import io.zaryx.Server;
import io.zaryx.content.commands.Command;
import io.zaryx.model.cycleevent.CycleEvent;
import io.zaryx.model.cycleevent.CycleEventContainer;
import io.zaryx.model.cycleevent.CycleEventHandler;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.entity.player.Right;
import io.zaryx.model.entity.player.mode.Mode;
import io.zaryx.model.entity.player.mode.ModeType;
import io.zaryx.util.discord.Discord;

/**
 * Moves the selected player home after a period of time.
 * 
 * @author Matt
 */
public class Stuck extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		
		if (Server.getMultiplayerSessionListener().inAnySession(c) || c.underAttackByPlayer > 0) {
			c.sendMessage("Finish what you are doing before doing this.");
			return;
		}
		
		if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
			c.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
			return;
		}
		
		if (!c.getPosition().inWild()) {
			c.sendMessage("You can only use this in the wilderness.");
			return;
		}

		if (!c.isStuck) {
			c.isStuck = true;
			List<Player> staff = PlayerHandler.nonNullStream().filter(Objects::nonNull).filter(p -> p.getRights().isOrInherits(Right.MODERATOR)).collect(Collectors.toList());
			
			if (staff.size() > 0) {
				Discord.writeServerSyncMessage("```[Stuck] " + c.getDisplayName() + "" + " is stuck, teleport and help them.```");
				c.sendMessage("@red@You've activated stuck command and the staff online has been notified.");
				c.sendMessage("@red@Your account will be moved home in approximately 2 minutes.");
				c.sendMessage("@red@You cannot attempt ANY actions whatsoever other than talking.");
				c.sendMessage("@red@Or else your timer will be reset..");
			} else {
				c.sendMessage("@red@You've activated stuck command and there are no staff-members online.");
				c.sendMessage("@red@Your account will be moved home in approximately 2 minutes.");
				c.sendMessage("@red@You cannot attempt ANY actions whatsoever other than talking.");
				c.sendMessage("@red@Or else your timer will be reset..");
			}
			
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					if (c.isDisconnected() || !c.isStuck) {
						container.stop();
						return;
					}
					if (c.isStuck) {
						if (c.getMode().equals(Mode.forType(ModeType.GROUP_WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.WILDYMAN))) {
							c.getPA().movePlayer(3135,3628,0);
						} else {
							c.getPlayerAssistant().movePlayer(3093, 3493, 0);
						}
						c.sendMessage("@red@Your account has been moved home.");
						c.isStuck = false;

					}
					container.stop();
				}

				@Override
				public void onStopped() {

				}
			}, 100); 

		} else {
			c.sendMessage("@red@You have already activated stuck command, stay patient.");

		}
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Teleports you home if you are ever stuck");
	}

}
