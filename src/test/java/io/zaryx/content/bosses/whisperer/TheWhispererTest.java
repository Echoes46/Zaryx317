package io.zaryx.content.bosses.whisperer;

import io.zaryx.model.CombatType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TheWhispererTest {

    @Test
    void specialsOnlyQueueAtTheThreeHealthThresholds() {
        assertFalse(TheWhisperer.shouldStartSpecial(900, 900, 0));
        assertFalse(TheWhisperer.shouldStartSpecial(721, 900, 0));
        assertTrue(TheWhisperer.shouldStartSpecial(720, 900, 0));
        assertFalse(TheWhisperer.shouldStartSpecial(496, 900, 1));
        assertTrue(TheWhisperer.shouldStartSpecial(495, 900, 1));
        assertFalse(TheWhisperer.shouldStartSpecial(271, 900, 2));
        assertTrue(TheWhisperer.shouldStartSpecial(270, 900, 2));
        assertFalse(TheWhisperer.shouldStartSpecial(1, 900, 3));
    }

    @Test
    void incomingDamageStopsAtEachSpecialThreshold() {
        assertEquals(75, TheWhisperer.capIncomingDamage(900, 900, 0, false, 500));
        assertEquals(5, TheWhisperer.capIncomingDamage(725, 900, 0, false, 500));
        assertEquals(0, TheWhisperer.capIncomingDamage(720, 900, 0, false, 50));
        assertEquals(75, TheWhisperer.capIncomingDamage(720, 900, 1, false, 500));
        assertEquals(5, TheWhisperer.capIncomingDamage(500, 900, 1, false, 500));
        assertEquals(0, TheWhisperer.capIncomingDamage(495, 900, 1, false, 50));
        assertEquals(75, TheWhisperer.capIncomingDamage(140, 900, 3, true, 500));
    }

    @Test
    void completedDeathCannotRestartTheEnragePhaseWhileWaitingToRespawn() {
        assertTrue(TheWhisperer.canBeginEnrage(false, false));
        assertFalse(TheWhisperer.canBeginEnrage(true, false));
        assertFalse(TheWhisperer.canBeginEnrage(false, true));
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
