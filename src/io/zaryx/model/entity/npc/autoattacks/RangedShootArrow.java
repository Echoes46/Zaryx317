package io.zaryx.model.entity.npc.autoattacks;

import io.zaryx.content.combat.npc.NPCAutoAttackBuilder;
import io.zaryx.model.Animation;
import io.zaryx.model.CombatType;
import io.zaryx.model.Graphic;
import io.zaryx.model.ProjectileBaseBuilder;

public class RangedShootArrow extends NPCAutoAttackBuilder {

    public RangedShootArrow(int maxHit) {
        setAttackDelay(3);
        setMaxHit(maxHit);
        setAnimation(new Animation(426));
        setCombatType(CombatType.RANGE);
        setDistanceRequiredForAttack(10);
        setHitDelay(3);
        setStartGraphic(new Graphic(19, Graphic.GraphicHeight.MIDDLE));
        setProjectile(new ProjectileBaseBuilder().setSendDelay(2).setProjectileId(11).createProjectileBase());
    }
}
