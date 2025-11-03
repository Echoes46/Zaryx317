package io.zaryx.content.bosses.kratos;

import io.zaryx.content.combat.Hitmark;
import io.zaryx.content.combat.melee.CombatPrayer;
import io.zaryx.content.combat.npc.NPCAutoAttack;
import io.zaryx.content.combat.npc.NPCAutoAttackBuilder;
import io.zaryx.content.combat.npc.NPCCombatAttack;
import io.zaryx.content.combat.npc.NPCCombatAttackHit;
import io.zaryx.model.Animation;
import io.zaryx.model.CombatType;
import io.zaryx.model.entity.player.Player;

import java.util.function.Consumer;
import java.util.function.Function;

public class SeldaehMelee implements Function<SeldaehNpc, NPCAutoAttack> {

    @Override
    public NPCAutoAttack apply(SeldaehNpc nightmare) {
        Consumer<NPCCombatAttackHit> onDamage = t -> {
            if (t.getCombatHit().missed())
                return;
            if (t.getVictim().isPlayer()) {
                Player player = (Player) t.getVictim();
                if (!CombatPrayer.isPrayerOn(player, CombatPrayer.PROTECT_FROM_MELEE)) {
                    t.getNpc().appendDamage(5, Hitmark.HEAL_PURPLE);
                    t.getNpc().getHealth().increase(15);
                }
            }
        };
        Consumer<NPCCombatAttack> onAttack = t -> {
            nightmare.attackCounter++;
        };
        return new NPCAutoAttackBuilder()
                .setAnimation(new Animation(4925))
                .setCombatType(CombatType.MELEE)
                .setSelectAutoAttack(attack -> attack.getNpc().distance(attack.getVictim().getPosition()) == 1)
                .setMaxHit(60)
                .setHitDelay(2)
                .setAttackDelay(4)
                .setDistanceRequiredForAttack(1)
                .setOnHit(onDamage)
                .setOnAttack(onAttack)
                .setSelectAutoAttack(npcCombatAttack -> npcCombatAttack.getNpc().distance(npcCombatAttack.getVictim().getPosition()) <= 1)
                .setPrayerProtectionPercentage(npcCombatAttack -> 0.2d)
                .createNPCAutoAttack();
    }
}