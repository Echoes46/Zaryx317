package io.zaryx.model.entity.npc.pets;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompanionFeedbackTest {
    @Test void trackerUsesXpWithinLevelAndHandlesCapAndPause() {
        assertArrayEquals(new String[]{"250 / 500 XP to level 2", "Next perk upgrade: level 3", "50"}, CompanionFeedback.progress(250, false));
        assertEquals("0 / 1000 XP to level 3", CompanionFeedback.progress(500, false)[0]);
        assertTrue(CompanionFeedback.progress(700, true)[1].contains("paused"));
        assertEquals("100", CompanionFeedback.progress(50000, false)[2]);
        assertTrue(CompanionFeedback.progress(50000, false)[0].contains("Maximum"));
    }
    @Test void milestoneFeedbackOnlyListsApplicableBonuses() {
        assertTrue(CompanionFeedback.upgrades(30122, 1, 2).isEmpty());
        assertEquals(1, CompanionFeedback.upgrades(1555, 2, 3).size());
        assertEquals(4, CompanionFeedback.upgrades(30122, 2, 3).size());
        assertTrue(CompanionFeedback.upgrades(30122, 9, 10).contains("Second wind: 8% -> 9%"));
        assertTrue(CompanionFeedback.upgrades(-1, 2, 3).isEmpty());
    }
}
