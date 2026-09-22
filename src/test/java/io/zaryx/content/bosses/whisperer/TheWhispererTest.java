package io.zaryx.content.bosses.whisperer;

import io.zaryx.model.CombatType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TheWhispererTest {

    @Test
    void specialsOnlyQueueAtTheThreeHealthThresholds() {
        assertFalse(TheWhisperer.shouldStartSpecial(700, 700, 0));
        assertFalse(TheWhisperer.shouldStartSpecial(561, 700, 0));
        assertTrue(TheWhisperer.shouldStartSpecial(560, 700, 0));
        assertFalse(TheWhisperer.shouldStartSpecial(386, 700, 1));
        assertTrue(TheWhisperer.shouldStartSpecial(385, 700, 1));
        assertFalse(TheWhisperer.shouldStartSpecial(211, 700, 2));
        assertTrue(TheWhisperer.shouldStartSpecial(210, 700, 2));
        assertFalse(TheWhisperer.shouldStartSpecial(1, 700, 3));
    }

    @Test
    void volleyChangesStyleOnlyAfterTheDocumentedSpecials() {
        assertVolley(0, CombatType.MAGE, CombatType.MAGE, CombatType.MAGE);
        assertVolley(1, CombatType.MAGE, CombatType.MAGE, CombatType.RANGE);
        assertVolley(2, CombatType.MAGE, CombatType.MAGE, CombatType.RANGE);
        assertVolley(3, CombatType.MAGE, CombatType.RANGE, CombatType.MAGE);
        assertEquals(CombatType.RANGE, TheWhisperer.volleyStyle(0, false, 0));
    }

    private static void assertVolley(int specialsCompleted, CombatType first, CombatType second, CombatType third) {
        assertEquals(first, TheWhisperer.volleyStyle(specialsCompleted, true, 0));
        assertEquals(second, TheWhisperer.volleyStyle(specialsCompleted, true, 1));
        assertEquals(third, TheWhisperer.volleyStyle(specialsCompleted, true, 2));
    }
}
