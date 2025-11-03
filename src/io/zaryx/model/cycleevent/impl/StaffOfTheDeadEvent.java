package io.zaryx.model.cycleevent.impl;

import io.zaryx.model.cycleevent.Event;
import io.zaryx.model.entity.player.Player;

public class StaffOfTheDeadEvent extends Event<Player> {

	public StaffOfTheDeadEvent(Player attachment) {
		super("staff_of_the_dead", attachment, 1);
	}

	@Override
	public void execute() {
		if (attachment == null || attachment.isDisconnected()) {
			super.stop();
			return;
		}
		if (attachment.playerEquipment[Player.playerWeapon] != 11791 && attachment.playerEquipment[Player.playerWeapon] != 12904 && attachment.playerEquipment[Player.playerWeapon] != 22296) {
			super.stop();
			return;
		}
		if (super.getElapsedTicks() > 100) {
			attachment.sendSpamMessage("Your protection fades away.");
			super.stop();
			return;
		}
	}

}
