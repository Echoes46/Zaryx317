package io.zaryx.content.boosts.xp;

import io.zaryx.content.boosts.PlayerSkillWrapper;
import io.zaryx.content.bosses.hespori.Hespori;
import io.zaryx.util.Misc;

public class AttasBoost extends ExperienceBooster {
    @Override
    public String getDescription() {
        return "+50% XP (" + Misc.cyclesToDottedTime((int) Hespori.ATTAS_TIMER) + ")";
    }

    @Override
    public boolean applied(PlayerSkillWrapper playerSkillWrapper) {
        return Hespori.ATTAS_TIMER > 0;
    }
}
