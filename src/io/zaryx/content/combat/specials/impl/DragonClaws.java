package io.zaryx.content.combat.specials.impl;

import io.zaryx.content.combat.Damage;
import io.zaryx.content.combat.Hitmark;
import io.zaryx.content.combat.formula.rework.MeleeCombatFormula;
import io.zaryx.content.combat.specials.Special;
import io.zaryx.content.skills.Skill;
import io.zaryx.model.CombatType;
import io.zaryx.model.SoundType;
import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerAssistant;
import io.zaryx.util.Misc;

public class DragonClaws extends Special {

	public DragonClaws() {
		super(5.0, 2.0, 1.0, new int[] { 20784, 26708, 28534 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.startAnimation(7514);
		player.gfx0(1171);
		player.getPA().sendSound(2537, SoundType.AREA_SOUND);
		int max = MeleeCombatFormula.get().getMaxHit(player, target, 1.0, 1.0);

		if (damage.getAmount() == 0) {
			int second = Misc.random(0, max);
			if (second == 0) {
				doHit(player, target, 0, 0);
				doHit(player, target, (int) (max * 0.75d), 1);
				doHit(player, target, (int) (max * 0.75d), 1);
			} else {
				doHit(player, target, second, 0);
				doHit(player, target, second / 2, 1);
				doHit(player, target, second / 2, 1);
			}
		} else {
			int halvedHit = damage.getAmount() == 0 ? 0 : damage.getAmount() / 2;
			int finalHit = halvedHit == 0 ? 0 : halvedHit / 2;
			doHit(player, target, halvedHit, 0);
			doHit(player, target, finalHit, 1);
			doHit(player, target, finalHit, 1);
		}
	}

	private void doHit(Player player, Entity target, int damage, int delay) {
		player.getDamageQueue().add(new Damage(target, damage, player.hitDelay + delay, player.playerEquipment, damage > 0 ? Hitmark.HIT : Hitmark.MISS, CombatType.MELEE));
		player.getPA().addXpDrop(new PlayerAssistant.XpDrop(damage, Skill.ATTACK.getId()));
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {

	}

}
