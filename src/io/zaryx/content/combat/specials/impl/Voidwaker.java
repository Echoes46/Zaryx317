package io.zaryx.content.combat.specials.impl;

import io.zaryx.content.combat.Damage;
import io.zaryx.content.combat.formula.rework.MeleeCombatFormula;
import io.zaryx.content.combat.specials.Special;
import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.Misc;

public class Voidwaker extends Special {
    public Voidwaker() {
        super(5.0, 1.0, 1.0, new int[] { 27690,28531  });
    }

    @Override
    public void activate(Player player, Entity target, Damage damage) {
        player.startAnimation(1378);
        if (target.isPlayer()) {
            target.asPlayer().gfx0(2363);
        } else if (target.isNPC()) {
            target.asNPC().gfx0(2363);
        }
        int maximumDamage = MeleeCombatFormula.get().getMaxHit(player, player, 1.0, 1.0);
        int damage2 = Misc.random((maximumDamage/2), (int) (maximumDamage*1.5));
        damage.setAmount(damage2);
    }

    @Override
    public void hit(Player player, Entity target, Damage damage) {

    }
}
