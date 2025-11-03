package io.zaryx.content.combat.effects.damageeffect.impl;

import java.util.Optional;

import io.zaryx.content.combat.Damage;
import io.zaryx.content.combat.effects.damageeffect.DamageEffect;
import io.zaryx.model.entity.HealthStatus;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.Misc;

public class SerpentineHelmEffect implements DamageEffect {

	@Override
	public void execute(Player attacker, Player defender, Damage damage) {
		attacker.getHealth().proposeStatus(HealthStatus.VENOM, 6, Optional.of(defender));
	}

	@Override
	public void execute(Player attacker, NPC defender, Damage damage) {
		defender.getHealth().proposeStatus(HealthStatus.VENOM, 6, Optional.of(attacker));
	}

	@Override
	public boolean isExecutable(Player operator) {
		return (operator.getItems().isWearingItem(12931) || operator.getItems().isWearingItem(13199) || operator.getItems().isWearingItem(13197)) && Misc.random(5) == 1;
	}

}
