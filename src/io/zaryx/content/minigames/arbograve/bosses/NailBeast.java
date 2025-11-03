package io.zaryx.content.minigames.arbograve.bosses;

import com.google.common.collect.Lists;
import io.zaryx.content.combat.npc.NPCAutoAttack;
import io.zaryx.content.combat.npc.NPCAutoAttackBuilder;
import io.zaryx.content.combat.npc.NPCCombatAttack;
import io.zaryx.content.instances.InstancedArea;
import io.zaryx.content.minigames.arbograve.ArbograveBoss;
import io.zaryx.model.Animation;
import io.zaryx.model.CombatType;
import io.zaryx.model.entity.npc.NPCSpawning;
import io.zaryx.model.entity.npc.stats.NpcBonus;
import io.zaryx.model.entity.player.Position;

import java.util.function.Function;

public class NailBeast extends ArbograveBoss {
    public NailBeast(Position position, InstancedArea instancedArea) {
        super(2948, new Position(position.getX(), position.getY(), instancedArea.getHeight()), instancedArea);
        revokeWalkingPrivilege = false;
        walkingType = 1;
        facePosition(1688, 4253);
        setAttacks();
        getCombatDefinition().setDefenceBonus(NpcBonus.RANGE_BONUS, 0);
        setNpcStats(NPCSpawning.getStats(2000, 0, 0));
    }

    @Override
    public void process() {
        setAttacks();
        super.process();
    }

    private void setAttacks() {
        setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(5989))
                        .setCombatType(CombatType.MELEE)
                        .setDistanceRequiredForAttack(2)
                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                        .setMultiAttack(true)
                        .setMaxHit(25)
                        .setAccuracyBonus(npcCombatAttack -> 10.0)
                        .setPrayerProtectionPercentage(new Function<NPCCombatAttack, Double>() {
                            @Override
                            public Double apply(NPCCombatAttack npcCombatAttack) {
                                return 0.75;
                            }
                        })
                        .setAttackDelay(4)
                        .createNPCAutoAttack()

        ));
    }
}
