package io.zaryx.content.combat.specials.impl;

import io.zaryx.content.combat.Damage;
import io.zaryx.content.combat.range.RangeData;
import io.zaryx.content.combat.specials.Special;
import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Player;

public class Ballista extends Special {

	public Ballista() {
		super(6.5, 1.25, 1.25, new int[] { 19478, 19481, 26712 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.usingBow = true;
		player.startAnimation(7222);
		if (player.playerAttackingIndex > 0 && target instanceof Player) {
			RangeData.fireProjectilePlayer(player, (Player) target, 50, 70, RangeData.getRangeProjectileGFX(player), 43, 31, 37, 10);
		} else if (player.npcAttackingIndex > 0 && target instanceof NPC) {
			RangeData.fireProjectileNpc(player, (NPC) target, 50, 70, RangeData.getRangeProjectileGFX(player), 43, 31, 37, 10);
		}
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {

	}
}
