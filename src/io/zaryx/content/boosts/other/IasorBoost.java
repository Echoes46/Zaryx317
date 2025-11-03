package io.zaryx.content.boosts.other;

import io.zaryx.content.bosses.hespori.Hespori;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.Misc;

public class IasorBoost extends GenericBoost {
    @Override
    public String getDescription() {
        return "+10% Drop Rate (" + Misc.cyclesToDottedTime((int) Hespori.IASOR_TIMER) + ")";
    }

    @Override
    public boolean applied(Player player) {
        return Hespori.IASOR_TIMER > 0;
    }
}
