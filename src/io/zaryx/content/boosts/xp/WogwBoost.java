package io.zaryx.content.boosts.xp;

import io.zaryx.content.boosts.PlayerSkillWrapper;
import io.zaryx.content.wogw.Wogw;
import io.zaryx.util.Misc;

public class WogwBoost extends ExperienceBooster {
    @Override
    public String getDescription() {
        return "+50% XP Rate (" + Misc.cyclesToDottedTime((int) Wogw.EXPERIENCE_TIMER) + ")";
    }

    @Override
    public boolean applied(PlayerSkillWrapper playerSkillWrapper) {
        return Wogw.EXPERIENCE_TIMER > 0;
    }
}
