package io.zaryx.model.entity.player.packets;

import io.zaryx.content.combat.magic.CombatSpellData;
import io.zaryx.content.combat.magic.LunarSpells;
import io.zaryx.model.entity.player.PacketType;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;

/**
 * Attack Player
 **/
public class AttackPlayer implements PacketType {

	public static final int ATTACK_PLAYER = 73, MAGE_PLAYER = 249;

	public static int[] WEAPONWILD = {
			33005, 28919, 20370, 20374, 22664,  20372, 20368, 39006, 27275, 33205,  33058, 33207, 20484, 33202, 33204, 28688, 26269, 26551, 33160, 33161, 33162, 25739, 25736, 26708, 25918, 26482, 25734, 33184, 33203, 33206, 33149, 27253,  12899, 12900, 33814, 33148, 33149, 26374, 25731, 28338, 20484, 20483, 20486, 28585, 25734, 27246, 28919, 33005, 39001, 39000, 39002, 33061, 27473, 27475, 27477, 27479, 27481, 33567, 22634, 22636
	};
	public static int[] ARMORWILD = {
			33153, 33154, 33155, 33150, 33151, 33152, 39004, 39005, 39010, 39007, 39008, 39009, 33199, 33200, 33201, 33009, 33001, 33002, 27235, 27238, 27241, 33141, 33142, 33143, 28254, 28256, 28258, 10559, 10556, 24664, 24666, 24668, 27428, 27430, 27432, 27434, 27436, 27438, 33149, 33183, 33186, 33187, 33188, 29999, 33144, 33145, 33146, 26382, 26384, 26386, 25398, 25389, 25401, 28869, 28173, 28171, 28169, 26469, 33800, 33802, 33804, 33806, 33808, 33810, 33812, 26471, 13681, 26235, 12892, 12893, 12894, 12895, 12896, 33064, 33059, 33063, 33060, 33062, 33061, 27473, 27475, 27477, 27479, 27481, 33567, 33000, 33001, 33002
	};
	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		if (player.getMovementState().isLocked() || player.getLock().cannotInteract(player))
			return;
		if (player.isFping()) {
			/**
			 * Cannot do action while fping
			 */
			return;
		}
		player.interruptActions();
		player.playerAttackingIndex = 0;
		player.npcAttackingIndex = 0;

		if (player.isForceMovementActive()) {
			return;
		}
		if (player.isNpc) {
			return;
		}
		switch (packetType) {
		case ATTACK_PLAYER:
			if (player.morphed || player.respawnTimer > 0) {
				return;
			}
			//player.stopMovement();
			int playerIndex = player.getInStream().readSignedWordBigEndian();

			String option = player.getPA().getPlayerOptions().getOrDefault(3, "null");
			player.debug(String.format("PlayerOption \"%s\" on player index %d.", option, player.playerAttackingIndex));

			PlayerHandler.getOptionalPlayerByIndex(playerIndex).ifPresentOrElse(player1 -> {
				if (player.getController().onPlayerOption(player, player1, option))
					return;

				player.usingClickCast = false;
				player.playerAttackingIndex = playerIndex;
				player.faceEntity(player1);

				if (player.getPA().viewingOtherBank) {
					player.getPA().resetOtherBank();
				}

				if (player.attacking.attackEntityCheck(player1, true)) {
					player.attackEntity(player1);
				} else {
					player.attacking.reset();
				}
			}, () -> player.attacking.reset());
			break;






		case MAGE_PLAYER:
			if (player.morphed || player.respawnTimer > 0) {
				player.attacking.reset();
				return;
			}

			player.stopMovement();
			player.playerAttackingIndex = player.getInStream().readSignedWordA();
			int castingSpellId = player.getInStream().readSignedWordBigEndian();
			player.usingClickCast = false;

			PlayerHandler.getOptionalPlayerByIndex(player.playerAttackingIndex).ifPresentOrElse(player1 -> {
				player.faceEntity(player1);

				if (player1.isTeleblocked() && castingSpellId == CombatSpellData.TELEBLOCK) {
					player.sendMessage("That player is already affected by this spell.");
					player.attacking.reset();
					return;
				}

				for (int r = 0; r < CombatSpellData.REDUCE_SPELLS.length; r++) {
					if (CombatSpellData.REDUCE_SPELLS[r] == castingSpellId) {
						if ((System.currentTimeMillis()
								- player1.reduceSpellDelay[r]) < CombatSpellData.REDUCE_SPELL_TIME[r]) {
							player.sendMessage("That player is currently immune to this spell.");
							player.attacking.reset();
							return;
						}
					}
				}

				if (castingSpellId > 30_000) {
					LunarSpells.CastingLunarOnPlayer(player, castingSpellId);
				}

				for (int i = 0; i < CombatSpellData.MAGIC_SPELLS.length; i++) {
					if (castingSpellId == CombatSpellData.MAGIC_SPELLS[i][0]) {
						if (player.attacking.attackEntityCheck(player1, true)) {
							player.attackEntity(player1);
							player.setSpellId(i);
							player.usingClickCast = true;
						} else {
							player.attacking.reset();
						}
						return;
					}
				}

				player.attacking.reset();
			}, () -> player.attacking.reset());
			break;

		}

	}

}
