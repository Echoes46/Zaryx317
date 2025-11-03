package io.zaryx.content.combat.scripts;

import io.zaryx.content.bosses.hydra.CombatProjectile;
import io.zaryx.content.combat.core.HitDispatcher;
import io.zaryx.content.combat.formula.rework.MagicCombatFormula;
import io.zaryx.content.combat.formula.rework.MeleeCombatFormula;
import io.zaryx.content.combat.formula.rework.RangeCombatFormula;
import io.zaryx.content.combat.scipts.CombatProjectileInterface;
import io.zaryx.model.CombatType;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.npc.stats.NpcBonus;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;

import java.rmi.UnexpectedException;

/**
 * @author Glaba
 * @project Exiled-Server
 * @social Discord: Glabay
 * @since 2024-06-04
 */
public abstract class CombatantNpc implements CombatProjectileInterface {

    protected static Player getTarget(NPC boss) {
        int targetId = boss.getPlayerAttackingIndex();
        return PlayerHandler.getPlayerByIndex(targetId);
    }

    protected static void sendCombatProjectile(NPC boss, CombatProjectile projectile) {
        int centerSize = (int) Math.ceil((double) boss.getSize() / 2);
        int nX = boss.getX() + centerSize;
        int nY = boss.getY() + centerSize;
        int offX = (nX - getTarget(boss).getX()) * -1;
        int offY = (nY - getTarget(boss).getY()) * -1;
    }

    protected static boolean successfulHit(NPC npc, Player target, CombatType type) {
        double accuracyRoll = 0.0;
        int npcAttackBonus = 0;

        var definition = npc.getCombatDefinition();
        if (definition != null) {
            NpcBonus bonus = type.equals(CombatType.MELEE) ? NpcBonus.ATTACK_BONUS
                    : type.equals(CombatType.RANGE) ? NpcBonus.ATTACK_RANGE_BONUS
                    : NpcBonus.ATTACK_MAGIC_BONUS;
            npcAttackBonus = definition.getAttackBonus(bonus);
        }
        try {
            switch (type) {
                case MELEE:
                    accuracyRoll = MeleeCombatFormula.get().getAccuracy(npc, target, npcAttackBonus, 1.0);
                    break;
                case RANGE:
                    accuracyRoll = RangeCombatFormula.STANDARD.getAccuracy(npc, target, npcAttackBonus, 1.0);
                    break;
                case SPECIAL:
                case MAGE:
                    accuracyRoll = MagicCombatFormula.STANDARD.getAccuracy(npc, target, npcAttackBonus, 1.0);
                    break;
                default:
                    throw new UnexpectedException("NPC attack accuracy roll not specified");
            }
        } catch (UnexpectedException e) {
            e.printStackTrace(System.err);
        }
        return accuracyRoll >= HitDispatcher.rand.nextDouble();
    }
}
