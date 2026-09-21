package io.zaryx.content.skills.slayer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SlayerItemRulesTest {
    @Test
    void imbuesEachHelmetToItsMatchingVariant() {
        assertEquals(11865, SlayerItemRules.imbuedHelmetId(11864));
        assertEquals(25900, SlayerItemRules.imbuedHelmetId(25898));
        assertEquals(25906, SlayerItemRules.imbuedHelmetId(25904));
        assertEquals(25912, SlayerItemRules.imbuedHelmetId(25910));
        assertEquals(-1, SlayerItemRules.imbuedHelmetId(1));
        assertEquals(25910, SlayerItemRules.firstHelmetToImbue(id -> id == 25910));
    }

    @Test
    void acceptsChargedAndDepletedUnnotedBlackMasks() {
        assertTrue(SlayerItemRules.isUnnotedBlackMask(8901));
        assertTrue(SlayerItemRules.isUnnotedBlackMask(8903));
        assertTrue(SlayerItemRules.isUnnotedBlackMask(8921));
        assertFalse(SlayerItemRules.isUnnotedBlackMask(8902));
        assertEquals(8915, SlayerItemRules.firstBlackMask(id -> id == 8915));
    }

    @Test
    void pointBoostsDoNotStackBeyondDouble() {
        assertEquals(1, SlayerItemRules.pointMultiplier(false, false));
        assertEquals(2, SlayerItemRules.pointMultiplier(true, false));
        assertEquals(2, SlayerItemRules.pointMultiplier(false, true));
        assertEquals(2, SlayerItemRules.pointMultiplier(true, true));
    }
}
