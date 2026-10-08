package io.zaryx.content.combat.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AttackNpcCheckTest {
    @Test void multiCombatAllowsDifferentNpcTargets() {
        assertFalse(AttackNpcCheck.blockedByOtherCombat(true, 0, 10, 11, 5935));
        assertFalse(AttackNpcCheck.blockedByOtherCombat(true, 5, 10, 11, 5935));
    }
    @Test void singlesAllowsCurrentNpcButBlocksOtherCombat() {
        assertFalse(AttackNpcCheck.blockedByOtherCombat(false, 0, 10, 10, 5935));
        assertTrue(AttackNpcCheck.blockedByOtherCombat(false, 0, 10, 11, 5935));
        assertTrue(AttackNpcCheck.blockedByOtherCombat(false, 5, 10, 10, 5935));
    }
    @Test void exceptionsUseNpcIdsRatherThanArrayIndexes() {
        assertFalse(AttackNpcCheck.blockedByOtherCombat(false, 0, 10, 11, 7514));
        assertTrue(AttackNpcCheck.blockedByOtherCombat(false, 0, 10, 7514, 5935));
    }
}
