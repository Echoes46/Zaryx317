package io.zaryx.content.minigames.arbograve.bosses;

import com.google.common.collect.Lists;
import io.zaryx.content.combat.Hitmark;
import io.zaryx.content.combat.npc.NPCAutoAttack;
import io.zaryx.content.combat.npc.NPCAutoAttackBuilder;
import io.zaryx.content.instances.InstancedArea;
import io.zaryx.content.minigames.arbograve.ArbograveBoss;
import io.zaryx.model.Animation;
import io.zaryx.model.CombatType;
import io.zaryx.model.entity.player.Position;
import io.zaryx.util.Misc;


public class ScarabSwarm extends ArbograveBoss {
    public ScarabSwarm(InstancedArea instancedArea) {
        super(1782, new Position(Misc.random(1709, 1717), Misc.random(4264, 4273), instancedArea.getHeight()), instancedArea);

        getBehaviour().setAggressive(true);
        getCombatDefinition().setAggressive(true);
    }

    @Override
    public void process() {
        setAttacks();
        super.process();
    }

    private void setAttacks() {
        setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(1946))
                        .setCombatType(CombatType.RANGE)
                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                        .setMultiAttack(true)
                        .setDistanceRequiredForAttack(1)
                        .setPoisonDamage(12)
                        .setMaxHit(0)
                        .setAccuracyBonus(npcCombatAttack -> 10.0)
                        .setOnAttack(npcCombatAttack -> {

                            asNPC().getInstance().getNpcs().forEach(npc -> {
                                if (npc.getNpcId() == 1127) {
                                    npc.appendHeal(3, Hitmark.HEAL_PURPLE);
                                }
                            });

                        })
                        .setHitDelay(4)
                        .setAttackDelay(4)
                        .createNPCAutoAttack()

        ));
    }
}
