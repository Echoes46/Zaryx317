package io.zaryx.content.minigames.arbograve.bosses;

import com.google.common.collect.Lists;
import io.zaryx.content.combat.npc.NPCAutoAttack;
import io.zaryx.content.combat.npc.NPCAutoAttackBuilder;
import io.zaryx.content.combat.npc.NPCCombatAttack;
import io.zaryx.content.instances.InstancedArea;
import io.zaryx.content.minigames.arbograve.ArbograveBoss;
import io.zaryx.model.Animation;
import io.zaryx.model.CombatType;
import io.zaryx.model.entity.player.Position;
import io.zaryx.util.Misc;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;


public class Scarab extends ArbograveBoss {
    public Scarab(InstancedArea instancedArea) {
        super(1127, new Position(1712, 4264, instancedArea.getHeight()), instancedArea);
        facePosition(1713, 4273);
        setAttacks();
    }

    @Override
    public void process() {
        setAttacks(); postDefend();
        super.process();
    }

    private long delay = 0;

    private void postDefend() {
        if (Misc.random(0,10) == 0 && delay < System.currentTimeMillis()) {
            for (int i = 0; i < 5 + (2 + asNPC().getInstance().getPlayers().size()); i++) {
                new ScarabSwarm(this.getInstance());
            }
            delay = (System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30));
        }
    }

    private void setAttacks() {
        setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(5457))
                        .setCombatType(CombatType.MELEE)
                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                        .setMultiAttack(true)
                        .setDistanceRequiredForAttack(3)
                        .setMaxHit(36)
                        .setAccuracyBonus(npcCombatAttack -> 10.0)
                        .setPrayerProtectionPercentage(new Function<NPCCombatAttack, Double>() {
                            @Override
                            public Double apply(NPCCombatAttack npcCombatAttack) {
                                return 0.75;
                            }
                        })
                        .setPoisonDamage(4)
                        .setAttackDelay(4)
                        .createNPCAutoAttack()

        ));
    }
}
