package io.zaryx.content.combat.effects.damageeffect.impl;

import java.util.Optional;

import io.zaryx.content.combat.Damage;
import io.zaryx.content.combat.effects.damageeffect.DamageEffect;
import io.zaryx.model.entity.HealthStatus;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.Misc;

public class ToxicBlowpipeEffect implements DamageEffect {

	@Override
	public void execute(Player attacker, Player defender, Damage damage) {
		defender.getHealth().proposeStatus(HealthStatus.VENOM, 6, Optional.of(attacker));
	}

	@Override
	public void execute(Player attacker, NPC defender, Damage damage) {
		defender.getHealth().proposeStatus(HealthStatus.VENOM, 6, Optional.of(attacker));
	}

	@Override
	public boolean isExecutable(Player operator) {
		return operator.getItems().isWearingItem(12926) && Misc.random(3) == 0 || operator.getItems().isWearingItem(28688) && Misc.random(3) == 0;
	}

}
