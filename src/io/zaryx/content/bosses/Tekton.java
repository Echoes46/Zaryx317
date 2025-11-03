package io.zaryx.content.bosses;

import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.npc.NPCHandler;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.Misc;

public class Tekton {

	public static void tektonSpecial(Player player) {
		NPC TEKTON = NPCHandler.getNpc(7544);

		if (TEKTON.isDead()) {
			return;
		}

		NPCHandler.npcs[TEKTON.getIndex()].forceChat("RAAAAAAAA!");
		TEKTON.underAttackBy = -1;
		TEKTON.underAttack = false;

		if (Misc.isLucky(5)) {
			DonorBoss3.burnGFX(player, TEKTON);
		}
	}
}
