package io.zaryx.content.combat.specials.impl;

import io.zaryx.Configuration;
import io.zaryx.content.combat.Damage;
import io.zaryx.content.combat.specials.Special;
import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.Misc;

/**
 * @author Jason MacKeigan
 * @date Apr 8, 2015, 2015, 10:45:54 AM
 */
public class ArcLight extends Special {

	public ArcLight() {
		super(5.0, 4.55, 1.3, new int[]{19675});
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.gfx0(483);
		player.startAnimation(2890);
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {

		if (damage.getAmount() > 0) {
			if (target instanceof Player) {
				Player playerTarget = ((Player) target);
				if (playerTarget.playerLevel[1] > 0) {
					playerTarget.playerLevel[1] -= ((Player) target).playerLevel[1] * 0.05;
					playerTarget.getPA().refreshSkill(1);
				}
			} else {
				NPC npc = ((NPC) target);
				if (player.debugMessage) {
					player.sendMessage("Dragon warhammer, npc defence before: " + npc.getDefence());
				}
				if (Misc.linearSearch(Configuration.DEMON_IDS, npc.getNpcId()) != -1) {
					npc.lowerDefence(0.1);
				} else {
					npc.lowerDefence(0.05);
				}
				if (player.debugMessage) {
					player.sendMessage("Dragon warhammer, npc defence after: " + npc.getDefence());
				}
			}
		}
	}
}


