package io.zaryx.content.boosts.xp;

import io.zaryx.content.boosts.BoostType;
import io.zaryx.content.boosts.Booster;
import io.zaryx.content.boosts.PlayerSkillWrapper;

public abstract class ExperienceBooster implements Booster<PlayerSkillWrapper> {

    @Override
    public BoostType getType() {
        return BoostType.EXPERIENCE;
    }

}
