package io.zaryx.model.entity.npc.drops;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UniqueDropAnnouncementTest {

    @Test
    void commonHydraBonesDoNotCountAsUniqueDrops() {
        assertFalse(TableGroup.isAlwaysAnnouncedDrop(22780, "hydra bone"));
        assertFalse(TableGroup.isAlwaysAnnouncedDrop(22781, "hydra bones"));
        assertTrue(TableGroup.isAlwaysAnnouncedDrop(22969, "hydra heart"));
        assertTrue(TableGroup.isAlwaysAnnouncedDrop(22988, "hydra leather"));
    }

    @Test
    void namedSpecialDropsUseTheirActualItemNames() {
        assertTrue(TableGroup.isAlwaysAnnouncedDrop(6737, "archers ring"));
        assertTrue(TableGroup.isAlwaysAnnouncedDrop(26358, "unknown"));
        assertFalse(TableGroup.isAlwaysAnnouncedDrop(995, "coins"));
    }

    @Test
    void ordinaryRareItemIdsAreNotBlockedByTheSpecialExclusions() {
        assertFalse(TableGroup.isExcludedRareDropId(22988));
        assertTrue(TableGroup.isExcludedRareDropId(23083));
        assertTrue(TableGroup.isExcludedRareDropId(23490));
    }
}
