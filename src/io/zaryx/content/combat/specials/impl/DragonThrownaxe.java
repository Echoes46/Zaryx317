package io.zaryx.content.combat.specials.impl;

import io.zaryx.content.combat.Damage;
import io.zaryx.content.combat.specials.Special;
import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.player.Player;

public class DragonThrownaxe extends Special {

	public DragonThrownaxe() {
		super(2.5, 1.25, 1.0, new int[] { 20849 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.startAnimation(7521);
		//player.gfx100(1318); gfx is broken, makes players go invisible
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {}
}
