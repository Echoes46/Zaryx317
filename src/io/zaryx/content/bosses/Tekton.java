package io.zaryx.content.bosses;

import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.Misc;

public class Tekton {

	public static void tektonSpecial(Player player, NPC tekton) {
		if (player == null || tekton == null || tekton.isDead() || !tekton.isRegistered()
				|| !player.sameInstance(tekton)) {
			return;
		}

		tekton.forceChat("RAAAAAAAA!");
		tekton.underAttackBy = -1;
		tekton.underAttack = false;

		if (Misc.isLucky(5)) {
			DonorBoss3.burnGFX(player, tekton);
		}
	}
}
