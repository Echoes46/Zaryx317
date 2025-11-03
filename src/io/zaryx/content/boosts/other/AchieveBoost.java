package io.zaryx.content.boosts.other;

import io.zaryx.content.bosses.hespori.Hespori;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.Misc;

public class AchieveBoost extends GenericBoost {
    @Override
    public String getDescription() {
        return "x2 Achieve Progress (" + Misc.cyclesToDottedTime((int) Hespori.ACHIEVE_TIMER) + ")";
    }

    @Override
    public boolean applied(Player player) {
        return Hespori.ACHIEVE_TIMER > 0;
    }
}

