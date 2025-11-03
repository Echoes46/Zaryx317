package io.zaryx.content.combat.specials.impl;

import io.zaryx.content.combat.Damage;
import io.zaryx.content.combat.specials.Special;
import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.player.Player;

public class OsmumtenFang extends Special {

    public OsmumtenFang() {
        super(5.0, 2.0, 1.50, new int[] { 26219, 27246, 33202 });
    }

    @Override
    public void activate(Player player, Entity target, Damage damage) {
        player.gfx0(2124);
        player.startAnimation(6118);
    }

    @Override
    public void hit(Player player, Entity target, Damage damage) {

    }
}
