package io.zaryx.content.boosts.other;

import io.zaryx.Configuration;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.Misc;

public class DoubleDropsBoost extends GenericBoost {
    @Override
    public String getDescription() {
        return "+100% Loot Rate (" + Misc.cyclesToDottedTime((int) Configuration.DOUBLE_DROPS_TIMER) + ")";
    }

    @Override
    public boolean applied(Player player) {
        return Configuration.DOUBLE_DROPS_TIMER > 0;
    }
}
