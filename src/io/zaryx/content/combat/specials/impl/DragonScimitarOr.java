package io.zaryx.content.combat.specials.impl;

import io.zaryx.content.combat.Damage;
import io.zaryx.content.combat.melee.CombatPrayer;
import io.zaryx.content.combat.specials.Special;
import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.player.Player;

public class DragonScimitarOr extends Special {

    public DragonScimitarOr() {
        /*
         * Parameters:
         * 1. Special attack cost
         * 2. Accuracy multiplier
         * 3. Damage multiplier
         * 4. Item IDs that use this special
         */
        super(6.0, 1.35, 1.15, new int[] { 20000 });
    }

    @Override
    public void activate(Player player, Entity target, Damage damage) {
        player.gfx100(347);
        player.startAnimation(1872);
    }

    @Override
    public void hit(Player player, Entity target, Damage damage) {
        if (target instanceof Player) {
            if (damage.getAmount() > 0) {
                CombatPrayer.resetOverHeads((Player) target);
            }
        }
    }
}