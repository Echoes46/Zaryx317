package io.zaryx.content.combat.specials.impl;

import io.zaryx.content.combat.Damage;
import io.zaryx.content.combat.specials.Special;
import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.player.Player;

public class GraniteMaulHandleSpecialAttack extends Special {

    public GraniteMaulHandleSpecialAttack() {
        super(5.0, 1, 1, new int[]{24225, 24227});
    }

    @Override
    public void activate(Player player, Entity target, Damage damage) {

    }

    @Override
    public void hit(Player player, Entity target, Damage damage) {

    }
}