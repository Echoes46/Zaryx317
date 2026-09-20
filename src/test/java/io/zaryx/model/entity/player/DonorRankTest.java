package io.zaryx.model.entity.player;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DonorRankTest {

    @Test
    void donationTotalsResolveToTheNewRankNames() {
        assertNull(RankUpgrade.forAmount(19));
        assertRank(20, RankUpgrade.AWAKENED, "Awakened");
        assertRank(50, RankUpgrade.RUNIC, "Runic");
        assertRank(100, RankUpgrade.MYSTIC, "Mystic");
        assertRank(250, RankUpgrade.ARCANE, "Arcane");
        assertRank(500, RankUpgrade.ELDRITCH, "Eldritch");
        assertRank(1_250, RankUpgrade.ASTRAL, "Astral");
        assertRank(2_500, RankUpgrade.ETHEREAL, "Ethereal");
        assertRank(4_000, RankUpgrade.CELESTIAL, "Celestial");
        assertRank(6_500, RankUpgrade.DIVINE, "Divine");
        assertRank(15_000, RankUpgrade.ETERNAL, "Eternal");
        assertEquals(RankUpgrade.ETERNAL, RankUpgrade.forAmount(Integer.MAX_VALUE));
    }

    private static void assertRank(int amount, RankUpgrade expected, String displayName) {
        RankUpgrade actual = RankUpgrade.forAmount(amount);
        assertEquals(expected, actual);
        assertEquals(displayName, actual.rights.toString());
        assertEquals(displayName, actual.rights.getFormattedName());
    }
}
