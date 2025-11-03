package io.zaryx.content.combat.specials.impl;

import io.zaryx.content.combat.Damage;
import io.zaryx.content.combat.specials.Special;
import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.player.Player;

public class SoulReaper extends Special {

    public SoulReaper() {
        super(2.5, 1.0, 1.25, new int[] { 28338 });
    }

    @Override
    public void activate(Player player, Entity target, Damage damage) {
        player.startAnimation(6147);
    }

    @Override
    public void hit(Player player, Entity target, Damage damage) {

    }
}
