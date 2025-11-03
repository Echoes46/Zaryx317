package io.zaryx.content.combat.specials.impl;

import io.zaryx.content.combat.Damage;
import io.zaryx.content.combat.specials.Special;
import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.player.Player;

public class StatiusWarhammer extends Special {

	public StatiusWarhammer() {
		super(3.5, 1.25, 0.25, new int[] { 22622 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.gfx0(1241);
		player.startAnimation(1378);
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {
		new DragonWarhammer().hit(player, target, damage);
	}

}
