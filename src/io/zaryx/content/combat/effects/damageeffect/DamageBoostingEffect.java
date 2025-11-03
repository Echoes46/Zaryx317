package io.zaryx.content.combat.effects.damageeffect;

import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.player.Player;

public interface DamageBoostingEffect extends DamageEffect {

    double getMaxHitBoost(Player attacker, Entity defender);

}
