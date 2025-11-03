package io.zaryx.content.combat.specials.impl;

import io.zaryx.content.combat.Damage;
import io.zaryx.content.combat.Hitmark;
import io.zaryx.content.combat.formula.rework.RangeCombatFormula;
import io.zaryx.content.combat.range.RangeData;
import io.zaryx.content.combat.specials.Special;
import io.zaryx.content.skills.Skill;
import io.zaryx.model.CombatType;
import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerAssistant;
import io.zaryx.util.Misc;

public class AscensionCrossbow extends Special {

    public AscensionCrossbow() {
        super(3.0, 1.5, 1.5, new int[] { 33206, 26269 });
    }

    @Override
    public void activate(Player player, Entity target, Damage damage) {
        player.usingBow = true;
        player.startAnimation(9166);
        if (player.playerAttackingIndex > 0 && target instanceof Player) {
            RangeData.fireProjectilePlayer(player, (Player) target, 50, 70, 2010, 43, 31, 37, 10);
        } else if (player.npcAttackingIndex > 0 && target instanceof NPC) {
            RangeData.fireProjectileNpc(player, (NPC) target, 50, 70, 2010, 43, 31, 37, 10);
        }
        if (damage.isSuccess()) {
            player.usingZaryteSpec = true;
        }
        int max = RangeCombatFormula.STANDARD.getMaxHit(player, target, 1.0, 1.0);

        if (damage.getAmount() == 0) {
            int second = Misc.random(0, max);
            if (second == 0) {
                doHit(player, target, 0, 0);
                doHit(player, target, (int) (max * 0.75d), 0);
                doHit(player, target, (int) (max * 0.75d), 0);
            } else {
                doHit(player, target, second, 0);
                doHit(player, target, second / 2, 1);
                doHit(player, target, second / 2, 1);
            }
        } else {
            int halvedHit = damage.getAmount() == 0 ? 0 : damage.getAmount() / 2;
            int finalHit = halvedHit == 0 ? 0 : halvedHit / 2;
            doHit(player, target, halvedHit, 0);
            doHit(player, target, finalHit, 0);
            doHit(player, target, finalHit, 0);
        }
    }

    private void doHit(Player player, Entity target, int damage, int delay) {
        player.getDamageQueue().add(new Damage(target, damage, player.hitDelay + delay, player.playerEquipment, damage > 0 ? Hitmark.HIT : Hitmark.MISS, CombatType.MELEE));
        player.getPA().addXpDrop(new PlayerAssistant.XpDrop(damage, Skill.ATTACK.getId()));
    }

    @Override
    public void hit(Player player, Entity target, Damage damage) {

    }
}
