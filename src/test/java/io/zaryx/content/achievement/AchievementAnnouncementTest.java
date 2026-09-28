package io.zaryx.content.achievement;

import org.junit.jupiter.api.Test;

import io.zaryx.content.achievement.Achievements.Achievement;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AchievementAnnouncementTest {

    @Test
    void onlyFinalStepOfEachAchievementChainIsAnnounced() {
        assertFalse(Achievements.isFinalTier(Achievement.NEWB_VOTER));
        assertFalse(Achievements.isFinalTier(Achievement.ADVANCED_VOTER));
        assertTrue(Achievements.isFinalTier(Achievement.EXTREME_VOTER));

        assertFalse(Achievements.isFinalTier(Achievement.The_Slayer));
        assertFalse(Achievements.isFinalTier(Achievement.SLAYER_DESTROYER));
        assertTrue(Achievements.isFinalTier(Achievement.SLAYER_EXPERT));

        assertFalse(Achievements.isFinalTier(Achievement.NEX));
        assertFalse(Achievements.isFinalTier(Achievement.NEX_MASTER));
        assertTrue(Achievements.isFinalTier(Achievement.NEX_GOD1));
    }

    @Test
    void standaloneAchievementsAreAnnounced() {
        assertTrue(Achievements.isFinalTier(Achievement.Voter));
        assertTrue(Achievements.isFinalTier(Achievement.MAX));
    }
}
