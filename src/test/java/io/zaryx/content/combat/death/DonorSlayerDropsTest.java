package io.zaryx.content.combat.death;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DonorSlayerDropsTest {
    @Test
    void normalKillsRetainOneInFourLootRolls() {
        assertEquals(1, NPCDeath.donorSlayerDropRolls(1, 0));
        for (int roll = 1; roll < 4; roll++) {
            assertEquals(0, NPCDeath.donorSlayerDropRolls(1, roll));
        }
    }

    @Test
    void doubleLootRetainsOneInTwoLootRolls() {
        for (int roll = 0; roll < 4; roll++) {
            assertEquals(roll < 2 ? 1 : 0, NPCDeath.donorSlayerDropRolls(2, roll));
        }
    }

    @Test
    void fractionalReductionPreservesExpectedRollsIncludingZeroAndWholeMultiples() {
        for (int rolls = 0; rolls <= 12; rolls++) {
            int total = 0;
            for (int quarter = 0; quarter < 4; quarter++) {
                int result = NPCDeath.donorSlayerDropRolls(rolls, quarter);
                assertTrue(result >= 0 && result <= rolls);
                total += result;
            }
            assertEquals(rolls, total, "Four equally likely outcomes must average one quarter of the input");
        }
    }
}
