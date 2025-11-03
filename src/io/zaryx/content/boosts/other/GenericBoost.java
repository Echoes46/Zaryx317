package io.zaryx.content.boosts.other;

import io.zaryx.content.boosts.BoostType;
import io.zaryx.content.boosts.Booster;
import io.zaryx.model.entity.player.Player;

public abstract class GenericBoost implements Booster<Player> {
    @Override
    public BoostType getType() {
        return BoostType.GENERIC;
    }
}
