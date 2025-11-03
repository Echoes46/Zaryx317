package io.zaryx.content.combat.specials.impl;

import io.zaryx.content.combat.Damage;
import io.zaryx.content.combat.specials.Special;
import io.zaryx.model.Items;
import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.player.Player;

public class GraniteMaulSpecialAttack extends Special {

	public GraniteMaulSpecialAttack() {
		super(6.0, 1, 1, new int[] {Items.GRANITE_MAUL, Items.GRANITE_MAUL_OR });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {

	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {

	}

}
