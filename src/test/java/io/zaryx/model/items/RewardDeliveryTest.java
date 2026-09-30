package io.zaryx.model.items;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class RewardDeliveryTest {
    @Test void existingStackWorksInFullInventory() {
        assertTrue(RewardDelivery.fitsInventory(new int[]{996}, new int[]{100},
                List.of(new GameItem(995, 25)), id -> id == 995));
    }

    @Test void nonStackablesNeedOneSlotPerItem() {
        assertFalse(RewardDelivery.fitsInventory(new int[2], new int[2],
                List.of(new GameItem(100, 3)), id -> false));
        assertTrue(RewardDelivery.fitsInventory(new int[3], new int[3],
                List.of(new GameItem(100, 3)), id -> false));
    }

    @Test void duplicateRewardStacksShareOneSlotButCannotOverflow() {
        assertTrue(RewardDelivery.fitsInventory(new int[1], new int[1],
                List.of(new GameItem(995, 2), new GameItem(995, 3)), id -> true));
        assertFalse(RewardDelivery.fitsInventory(new int[1], new int[1],
                List.of(new GameItem(995, Integer.MAX_VALUE), new GameItem(995, 1)), id -> true));
    }

    @Test void bundleCheckDoesNotMutateInventoryOrAllowPartialFit() {
        int[] ids = {996, 0}, amounts = {10, 0};
        assertFalse(RewardDelivery.fitsInventory(ids, amounts,
                List.of(new GameItem(995, 5), new GameItem(100, 2)), id -> id == 995));
        assertArrayEquals(new int[]{996, 0}, ids);
        assertArrayEquals(new int[]{10, 0}, amounts);
    }

    @Test void stackLimitIsInclusive() {
        assertTrue(RewardDelivery.fitsInventory(new int[]{996}, new int[]{Integer.MAX_VALUE - 1},
                List.of(new GameItem(995, 1)), id -> true));
        assertFalse(RewardDelivery.fitsInventory(new int[]{996}, new int[]{Integer.MAX_VALUE},
                List.of(new GameItem(995, 1)), id -> true));
    }

    @Test void fullBankAcceptsExistingStacksAndPlaceholders() {
        assertTrue(RewardDelivery.fitsBank(Map.of(995, 10L, 100, 0L), 0, Map.of(995, 5L, 100, 3L)));
        assertFalse(RewardDelivery.fitsBank(Map.of(995, 10L), 0, Map.of(100, 1L)));
    }

    @Test void bankCapacityIsSharedAcrossAllRewards() {
        assertFalse(RewardDelivery.fitsBank(Map.of(), 1, Map.of(100, 1L, 101, 1L)));
        assertTrue(RewardDelivery.fitsBank(Map.of(), 2, Map.of(100, 100L, 101, 100L)));
    }

    @Test void bankRejectsOverflowWithoutClampingReward() {
        assertTrue(RewardDelivery.fitsBank(Map.of(995, (long) Integer.MAX_VALUE - 1), 0, Map.of(995, 1L)));
        assertFalse(RewardDelivery.fitsBank(Map.of(995, (long) Integer.MAX_VALUE), 100, Map.of(995, 1L)));
    }

    @Test void notesAndUnnotedRewardsShareBankCapacityAndStackLimit() {
        Map<Integer, Long> additions = RewardDelivery.bankAdditions(
                List.of(new GameItem(100, Integer.MAX_VALUE), new GameItem(101, 1)), id -> id == 101 ? 100 : id);
        assertEquals(2147483648L, additions.get(100));
        assertFalse(RewardDelivery.fitsBank(Map.of(), 1, additions));
    }
}
