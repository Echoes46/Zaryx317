package io.zaryx.content.bosses.sarachnis;

import io.zaryx.content.combat.npc.NPCAutoAttack;
import io.zaryx.content.combat.npc.NPCAutoAttackBuilder;
import io.zaryx.content.combat.npc.NPCCombatAttack;
import io.zaryx.model.Animation;
import io.zaryx.model.CombatType;

import java.util.function.Function;

public class SarachnisMinionMelee implements Function<SarachnisNpc, NPCAutoAttack> {

    @Override
    public NPCAutoAttack apply(SarachnisNpc nightmare) {
        return new NPCAutoAttackBuilder()
                .setAnimation(new Animation(8147))
                .setCombatType(CombatType.MELEE)
                .setMaxHit(13)
                .setHitDelay(2)
                .setAttackDelay(4)
                .setDistanceRequiredForAttack(1)
                .setPrayerProtectionPercentage(new Function<NPCCombatAttack, Double>() {
                    @Override
                    public Double apply(NPCCombatAttack npcCombatAttack) {
                        return 0.2d;
                    }
                })
                .createNPCAutoAttack();
    }
}