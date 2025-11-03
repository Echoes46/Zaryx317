package io.zaryx.util.logging.player;

import io.zaryx.content.achievement.Achievements;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.Misc;
import io.zaryx.util.logging.PlayerLog;

import java.util.Set;

public class ClaimAchievementLog extends PlayerLog {

    private final Achievements.Achievement achievement;

    public ClaimAchievementLog(Player player, Achievements.Achievement achievement) {
        super(player);
        this.achievement = achievement;
    }

    @Override
    public Set<String> getLogFileNames() {
        return Set.of("claimed_achievement");
    }

    @Override
    public String getLoggedMessage() {
        return Misc.replaceBracketsWithArguments("Claimed achievement {}", achievement);
    }
}
