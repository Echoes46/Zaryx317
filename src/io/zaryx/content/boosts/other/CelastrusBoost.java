package io.zaryx.content.boosts.other;

import io.zaryx.content.bosses.hespori.Hespori;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.Misc;

public class CelastrusBoost extends GenericBoost {
    @Override
    public String getDescription() {
        return "x2 Brimstone Keys (" + Misc.cyclesToDottedTime((int) Hespori.CELASTRUS_TIMER) + ")";
    }

    @Override
    public boolean applied(Player player) {
        return Hespori.CELASTRUS_TIMER > 0;
    }
}
