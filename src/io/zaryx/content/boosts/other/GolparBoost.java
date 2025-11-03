package io.zaryx.content.boosts.other;

import io.zaryx.content.bosses.hespori.Hespori;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.Misc;

public class GolparBoost extends GenericBoost {
    @Override
    public String getDescription() {
        return "x2 Bonus Loot (" + Misc.cyclesToDottedTime((int) Hespori.GOLPAR_TIMER) + ")";
    }

    @Override
    public boolean applied(Player player) {
        return Hespori.GOLPAR_TIMER > 0;
    }
}
