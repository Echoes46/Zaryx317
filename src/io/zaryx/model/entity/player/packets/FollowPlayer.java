package io.zaryx.model.entity.player.packets;

import io.zaryx.Configuration;
import io.zaryx.model.entity.player.PacketType;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;

public class FollowPlayer implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		if (c.getMovementState().isLocked() || c.getLock().cannotInteract(c))
			return;
		if (c.isFping()) {
			/**
			 * Cannot do action while fping
			 */
			return;
		}
		c.interruptActions();
		int followPlayer = c.getInStream().readUnsignedWordBigEndian();
		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			return;
		}
		if (c.getBankPin().requiresUnlock()) {
			c.getBankPin().open(2);
			return;
		}
		if (c.isNpc) {
			return;
		}
		if (followPlayer > Configuration.MAX_PLAYERS) {
			return;
		}
		if (PlayerHandler.players[followPlayer] != null) {
			c.playerAttackingIndex = 0;
			c.npcAttackingIndex = 0;
			c.usingBow = false;
			c.usingRangeWeapon = false;
			c.followDistance = 1;
			c.playerFollowingIndex = followPlayer;
			c.combatFollowing = false;
		}
	}
}