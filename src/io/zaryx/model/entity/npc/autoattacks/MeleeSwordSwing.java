package io.zaryx.model.entity.npc.autoattacks;

import io.zaryx.content.combat.npc.NPCAutoAttackBuilder;
import io.zaryx.model.Animation;
import io.zaryx.model.CombatType;

public class MeleeSwordSwing extends NPCAutoAttackBuilder {

    public MeleeSwordSwing(int maxHit) {
        setAttackDelay(4);
        setMaxHit(maxHit);
        setAnimation(new Animation(451));
        setCombatType(CombatType.MELEE);
    }
}
