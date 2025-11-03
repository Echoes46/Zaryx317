package io.zaryx.model.entity.player.packets;

import java.util.Objects;

import io.zaryx.Server;
import io.zaryx.content.combat.magic.CombatSpellData;
import io.zaryx.content.skills.crafting.OrbCharging;
import io.zaryx.model.entity.player.PacketType;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.zaryx.model.multiplayersession.MultiplayerSessionStage;
import io.zaryx.model.multiplayersession.MultiplayerSessionType;
import io.zaryx.model.multiplayersession.duel.DuelSession;

public class MagicOnObject implements PacketType {

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
//		int x = c.getInStream().readSignedWordBigEndian();
//		int y = c.getInStream().readSignedWordBigEndianA();
//		int magicId = c.getInStream().readUnsignedWord();
		
//		c.objectX = c.getInStream().readSignedWordBigEndianA();
//		c.objectId = c.getInStream().readUnsignedWord();
//		c.objectY = c.getInStream().readUnsignedWordA();
		
		int x = c.getInStream().readSignedWordBigEndian();
		int magicId = c.getInStream().readUnsignedWord();
		int y = c.getInStream().readUnsignedWordA();
		int objectId = c.getInStream().readInteger();
		

		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			return;
		}
		if (c.getBankPin().requiresUnlock()) {
			c.getBankPin().open(2);
			return;
		}
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
				MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
				&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
			c.sendMessage("Your actions have declined the duel.");
			duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
			duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}
		
		c.facePosition(x, y);

		System.out.println("Spell ID: " + magicId);
		System.out.println("Object used on: X: " + x + ", Y: " + y + ", ID: " + objectId);
		
		c.usingMagic = true;
		
		switch (objectId) {
			case 49185:
				if (c.getDungInstance() != null) { // Check if the player is in a dungeoneering instance
					if (CombatSpellData.fireSpells(c)) { // Check if the spell is a fire-based spell
						Server.getGlobalObjects().remove(objectId, x, y, c.getHeight());
						c.sendMessage("You cast a fire spell on the object and destroy it.");
					} else {
						c.sendMessage("You need to use a fire spell to destroy this object.");
					}
				} else {
					// Send a message to the player
					c.sendMessage("You need to be in a dungeoneering instance to destroy this object.");
				}
				break;

		case 2153:
		case 2152:
		case 2151:
		case 2150:
			OrbCharging.chargeOrbs(c, magicId, objectId);
			break;
		}

	}

}
